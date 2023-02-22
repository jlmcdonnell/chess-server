package dev.mcd.chess.serializer

import dev.mcd.chess.game.GameSession
import dev.mcd.chess.game.SessionId
import dev.mcd.chess.game.State
import kotlinx.serialization.Serializable

@Serializable
data class SessionInfoSerializer(
    val sessionId: SessionId,
    val whiteUserId: String,
    val blackUserId: String,
    val state: State,
    val board: BoardStateSerializer,
)

fun GameSession.sessionInfoSerializer() = SessionInfoSerializer(
    sessionId = sessionId,
    whiteUserId = playerWhite,
    blackUserId = playerBlack,
    state = state,
    board = board.boardState()
)
