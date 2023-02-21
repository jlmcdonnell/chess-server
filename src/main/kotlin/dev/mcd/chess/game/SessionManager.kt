package dev.mcd.chess.game

import dev.mcd.chess.game.model.GameSession
import dev.mcd.chess.game.model.SessionId
import io.ktor.server.plugins.NotFoundException

interface SessionManager {
    fun session(id: SessionId): GameSession
    fun add(session: GameSession)
    fun remove(session: GameSession)
}

class SessionManagerImpl : SessionManager {

    private val sessions = mutableListOf<GameSession>()

    override fun add(session: GameSession) {
        if (sessions.none { session.sessionId == it.sessionId }) {
            sessions += session
        } else {
            throw Error("Session with ID ${session.sessionId} already exists")
        }
    }

    override fun remove(session: GameSession) {
        sessions -= session
    }

    override fun session(id: SessionId): GameSession {
        return sessions.firstOrNull { it.sessionId == id } ?: throw NotFoundException("Session with ID $id not found")
    }
}
