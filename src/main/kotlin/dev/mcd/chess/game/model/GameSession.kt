package dev.mcd.chess.game.model

import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.auth.user.UserId

data class GameSession(
    val sessionId: String,
    val playerWhite: UserId,
    val playerBlack: UserId,
    val board: Board,
    var state: State = State.Started,
)

enum class State {
    Started,
    Terminated,
}
