package dev.mcd.chess.serializer

import com.github.bhlangonijr.chesslib.move.Move
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
    MoveHistory,
    Move,
    ErrorNotUsersMove,
    ErrorGameTerminated,
    ErrorInvalidMove,
}

fun Move.moveMessage() = GameMessage(MessageType.Move, DefaultJson.encodeToString(MoveSerializer(toString())))

fun GameSession.sessionInfoMessage(): GameMessage {
    return GameMessage(
        message = MessageType.SessionInfo,
        content = DefaultJson.encodeToString(sessionInfoSerializer())
    )
}

fun GameSession.moveHistoryMessage(): GameMessage {
    return GameMessage(
        message = MessageType.MoveHistory,
        content = DefaultJson.encodeToString(moveHistorySerializer())
    )
}
