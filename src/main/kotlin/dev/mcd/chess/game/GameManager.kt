package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.BoardEventListener
import com.github.bhlangonijr.chesslib.BoardEventType
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.auth.UserId
import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface GameManager {
    fun getGameUpdates(id: GameId): Flow<GameSession>
    fun getMoves(id: GameId): Flow<Move>
    fun getGame(id: GameId): GameSession
    fun getActiveGamesForUser(userId: UserId): List<GameSession>
    suspend fun add(session: GameSession)
    suspend fun remove(session: GameSession)
    suspend fun notifyUpdate(session: GameSession)
}

class SessionManagerImpl : GameManager {

    private val sessions = hashMapOf<GameId, GameSession>()
    private val lock = Mutex()
    private val _updates = MutableSharedFlow<GameSession>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1,
    )

    private val boardListeners = mutableMapOf<GameId, Job>()
    private val _moves = MutableSharedFlow<Pair<GameId, Move>>()

    override suspend fun add(session: GameSession) {
        lock.withLock {
            if (sessions[session.id] != null) {
                throw Error("Session with ID ${session.id} already exists")
            } else {
                sessions[session.id] = session
                _updates.emit(session)
                clearAndUpdateBoardListener(session.id, session.game.board)
            }
        }
    }

    override suspend fun remove(session: GameSession) {
        lock.withLock {
            sessions.remove(session.id)
            clearAndUpdateBoardListener(session.id, session.game.board)
        }
    }

    override fun getGame(id: GameId): GameSession {
        return sessions[id] ?: throw NotFoundException("Session with ID $id not found")
    }

    override fun getActiveGamesForUser(userId: UserId): List<GameSession> {
        return sessions.filterValues {
            it.game.blackPlayer.id == userId || it.game.whitePlayer.id == userId
        }.mapNotNull { it.value }
    }

    override suspend fun notifyUpdate(session: GameSession) {
        lock.withLock {
            sessions[session.id] = session
            _updates.emit(session)
            clearAndUpdateBoardListener(session.id, session.game.board)
        }
    }

    override fun getGameUpdates(id: GameId) = _updates.filter { it.id == id }

    override fun getMoves(id: GameId): Flow<Move> {
        return _moves.mapNotNull { if (it.first == id) it.second else null }
    }

    private fun clearAndUpdateBoardListener(id: GameId, newBoard: Board) {
        println("Updating Board Listener: $id")
        boardListeners[id]?.cancel()
        boardListeners[id] = CoroutineScope(Dispatchers.Default).launch {
            val listener = BoardEventListener { move ->
                move as Move
                println("Move: $move")
                runBlocking {
                    _moves.emit(id to move)
                }
            }
            try {
                newBoard.addEventListener(BoardEventType.ON_MOVE, listener)
                awaitCancellation()
            } finally {
                newBoard.removeEventListener(BoardEventType.ON_MOVE, listener)
            }
        }
    }
}
