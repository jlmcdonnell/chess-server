package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.Game
import com.github.bhlangonijr.chesslib.game.GameResult
import com.github.bhlangonijr.chesslib.game.Termination
import com.github.bhlangonijr.chesslib.move.MoveList

data class GameSession(
    val id: GameId,
    val game: Game,
)

val GameSession.isTerminated get() = game.result != GameResult.ONGOING

fun GameSession.resignForSide(side: Side) {
    game.result = if (side == Side.WHITE) GameResult.BLACK_WON else GameResult.WHITE_WON
    game.termination = Termination.NORMAL
}

fun GameSession.updateResultAndTermination(side: Side): Boolean {
    if (game.board.isMated) {
        game.result = if (side == Side.WHITE) GameResult.WHITE_WON else GameResult.BLACK_WON
        game.termination = Termination.NORMAL
        return true
    } else if (game.board.isDraw) {
        GameResult.DRAW
        game.result = GameResult.DRAW
        game.termination = Termination.NORMAL
        return true
    } else GameResult.ONGOING
    return false
}

fun GameSession.toPgn(): String {
    return game.toPgn(true, true)
}
