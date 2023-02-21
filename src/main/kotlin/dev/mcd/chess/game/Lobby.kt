package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.auth.user.UserId
import dev.mcd.chess.game.model.GameSession
import dev.mcd.chess.game.model.SessionId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import java.util.UUID

interface Lobby {
    suspend fun awaitSession(userId: UserId): SessionId
}

class LobbyImpl(private val sessionManager: SessionManager) : Lobby {
    private var waitingUsers = mutableListOf<Pair<UserId, CompletableDeferred<SessionId>>>()
    private val lock = Mutex()

    override suspend fun awaitSession(userId: UserId): SessionId {
        lock.lock()
        println("Locked for $userId")

        // Check if already in lobby and wait
        waitingUsers.firstOrNull { it.first == userId }?.let { (_, completable) ->
            lock.unlock()
            return completable.await()
        }

        // Check if anyone else is waiting and make match
        waitingUsers.removeFirstOrNull()?.let { (otherUser, otherCompletable) ->
            val sessionId = UUID.randomUUID().toString()
            val white = if (Math.random() > 0.5) otherUser else userId
            val session = GameSession(
                sessionId = sessionId,
                playerWhite = white,
                playerBlack = if (otherUser == white) userId else otherUser,
                board = Board(),
            )
            sessionManager.add(session)
            otherCompletable.complete(session.sessionId)
            lock.unlock()
            return sessionId
        }

        // We're sad and alone, wait for someone else
        val completable = CompletableDeferred<SessionId>()
        waitingUsers += userId to completable
        lock.unlock()
        return completable.await()
    }
}