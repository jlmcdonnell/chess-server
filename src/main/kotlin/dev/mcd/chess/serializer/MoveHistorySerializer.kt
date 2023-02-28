package dev.mcd.chess.serializer

import dev.mcd.chess.game.GameSession
import kotlinx.serialization.Serializable

@Serializable
data class MoveHistorySerializer(
    val fen: String,
    val moveList: List<String>,
)

fun GameSession.moveHistorySerializer() = MoveHistorySerializer(
    fen = board.fen,
    moveList = board.backup.map { it.move.toString() },
)
