package dev.mcd.chess.game

import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface SessionManager {
    fun updates(id: SessionId): Flow<GameSession>
    fun session(id: SessionId): GameSession
    suspend fun add(session: GameSession)
    suspend fun remove(session: GameSession)
    suspend fun update(session: GameSession)
}

class SessionManagerImpl : SessionManager {

    private val sessions = hashMapOf<SessionId, GameSession>()
    private val lock = Mutex()
    private val _updates = MutableSharedFlow<GameSession>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1,
    )

    override suspend fun add(session: GameSession) {
        lock.withLock {
            if (sessions[session.sessionId] != null) {
                throw Error("Session with ID ${session.sessionId} already exists")
            } else {
                sessions[session.sessionId] = session
                _updates.emit(session)
            }
        }
    }

    override suspend fun remove(session: GameSession) {
        lock.withLock {
            sessions.remove(session.sessionId)
        }
    }

    override fun session(id: SessionId): GameSession {
        return sessions[id] ?: throw NotFoundException("Session with ID $id not found")
    }

    override suspend fun update(session: GameSession) {
        lock.withLock {
            sessions[session.sessionId] = session
            _updates.emit(session)
        }
    }

    override fun updates(id: SessionId) = _updates.filter { it.sessionId == id }
}
