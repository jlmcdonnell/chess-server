package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.auth.UserId

data class GameSession(
    val sessionId: String,
    val playerWhite: UserId,
    val playerBlack: UserId,
    val board: Board,
    val state: State = State.STARTED,
)

enum class State {
    STARTED,
    DRAW,
    WHITE_RESIGNED,
    BLACK_RESIGNED,
    WHITE_CHECKMATED,
    BLACK_CHECKMATED;

    companion object {
        fun checkmated(side: Side) = if (side == Side.WHITE) {
            WHITE_CHECKMATED
        } else {
            BLACK_CHECKMATED
        }

        fun resigned(side: Side) = if (side == Side.WHITE) {
            WHITE_RESIGNED
        } else {
            BLACK_RESIGNED
        }
    }
}
