package dev.mcd.chess.game

import dev.mcd.chess.auth.UserId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Lobby {
    suspend fun awaitSession(userId: UserId): GameId
    suspend fun leave(userId: UserId)
    fun count(filter: UserId? = null): Int
}

class LobbyImpl(
    private val sessionManager: GameManager,
    private val gameFactory: GameFactory,
) : Lobby {

    private var waitingUsers = mutableListOf<Pair<UserId, CompletableDeferred<GameId>>>()
    private val lock = Mutex()

    override fun count(filter: UserId?): Int {
        val totalSize = waitingUsers.size
        return if (filter == null) {
            totalSize
        } else {
            totalSize - waitingUsers.count { (userId, _) ->
                userId == filter
            }
        }
    }

    override suspend fun awaitSession(userId: UserId): GameId {
        lock.lock()
        println("Locked for $userId")

        // Check if already in lobby and wait
        waitingUsers.firstOrNull { it.first == userId }?.let { (_, completable) ->
            lock.unlock()
            return completable.await()
        }

        // Check if anyone else is waiting and make match
        waitingUsers.removeFirstOrNull()?.let { (otherUser, otherCompletable) ->
            val session = gameFactory.generateGame(otherUser, userId)
            sessionManager.add(session)
            otherCompletable.complete(session.id)
            lock.unlock()
            return session.id
        }

        // We're sad and alone, wait for someone else
        val completable = CompletableDeferred<GameId>()
        waitingUsers += userId to completable
        lock.unlock()
        return completable.await()
    }

    override suspend fun leave(userId: UserId) {
        lock.withLock {
            waitingUsers.removeIf { (user, completable) ->
                (user == userId).also { completable.cancel() }
            }
        }
    }
}
