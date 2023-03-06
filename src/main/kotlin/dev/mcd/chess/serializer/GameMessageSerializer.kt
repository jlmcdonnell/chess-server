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
    GameState,
    Move,
    ErrorNotUsersMove,
    ErrorGameTerminated,
    ErrorInvalidMove,
}

fun String.moveMessage(count: Int): GameMessage {
    return GameMessage(
        message = MessageType.Move,
        content = DefaultJson.encodeToString(MoveSerializer(move = this, count = count))
    )
}

fun GameSession.gameStateMessage(): GameMessage {
    return GameMessage(
        message = MessageType.GameState,
        content = DefaultJson.encodeToString(gameInfoSerializer())
    )
}
