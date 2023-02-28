package dev.mcd.chess.serializer

import dev.mcd.chess.game.GameSession
import dev.mcd.chess.game.GameId
import dev.mcd.chess.game.SessionState
import kotlinx.serialization.Serializable

@Serializable
data class SessionInfoSerializer(
    val sessionId: GameId,
    val whiteUserId: String,
    val blackUserId: String,
    val state: SessionState,
)

fun GameSession.sessionInfoSerializer() = SessionInfoSerializer(
    sessionId = sessionId,
    whiteUserId = playerWhite,
    blackUserId = playerBlack,
    state = state,
)

