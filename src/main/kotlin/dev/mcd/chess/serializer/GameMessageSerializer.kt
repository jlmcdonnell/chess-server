package dev.mcd.chess.serializer

import dev.mcd.chess.game.GameSession
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class GameMessage(
    val message: MessageType,
    val content: String? = null,
)

@Serializable
enum class MessageType {
    SessionInfo,
    ErrorNotUsersMove,
    ErrorGameTerminated,
    ErrorInvalidMove,
}

fun GameSession.sessionInfoMessage(): GameMessage {
    return GameMessage(
        message = MessageType.SessionInfo,
        content = DefaultJson.encodeToString(sessionInfoSerializer())
    )
}
