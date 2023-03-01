package dev.mcd.chess.game

import dev.mcd.chess.auth.UserId
import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface GameManager {
    fun getGameUpdates(id: GameId): Flow<GameSession>
    fun getGame(id: GameId): GameSession
    fun getActiveGamesForUser(userId: UserId): List<GameSession>
    suspend fun add(game: GameSession)
    suspend fun remove(game: GameSession)
    suspend fun update(game: GameSession)
}

class SessionManagerImpl : GameManager {

    private val sessions = hashMapOf<GameId, GameSession>()
    private val lock = Mutex()
    private val _updates = MutableSharedFlow<GameSession>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1,
    )

    override suspend fun add(game: GameSession) {
        lock.withLock {
            if (sessions[game.sessionId] != null) {
                throw Error("Session with ID ${game.sessionId} already exists")
            } else {
                sessions[game.sessionId] = game
                _updates.emit(game)
            }
        }
    }

    override suspend fun remove(game: GameSession) {
        lock.withLock {
            sessions.remove(game.sessionId)
        }
    }

    override fun getGame(id: GameId): GameSession {
        return sessions[id] ?: throw NotFoundException("Session with ID $id not found")
    }

    override fun getActiveGamesForUser(userId: UserId): List<GameSession> {
        return sessions.filterValues {
            it.playerBlack == userId || it.playerWhite == userId
        }.mapNotNull { it.value }
    }

    override suspend fun update(game: GameSession) {
        lock.withLock {
            sessions[game.sessionId] = game
            _updates.emit(game)
        }
    }

    override fun getGameUpdates(id: GameId) = _updates.filter { it.sessionId == id }
}
