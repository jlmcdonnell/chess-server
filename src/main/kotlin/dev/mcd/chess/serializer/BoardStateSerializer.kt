package dev.mcd.chess.serializer

import com.github.bhlangonijr.chesslib.Board
import kotlinx.serialization.Serializable

@Serializable
data class BoardStateSerializer(
    val fen: String,
    val lastMoveSide: String?,
    val lastMoveSan: String?,
    val moveCount: Int,
)

fun Board.boardState(): BoardStateSerializer {
    val moveCount = moveCounter
    val fen = fen
    val lastMove = backup.lastOrNull()
    val lastMoveSan = lastMove?.move?.toString()
    val lastMoveSide = lastMove?.sideToMove?.name
    return BoardStateSerializer(
        fen = fen,
        moveCount = moveCount,
        lastMoveSide = lastMoveSide,
        lastMoveSan = lastMoveSan,
    )
}
