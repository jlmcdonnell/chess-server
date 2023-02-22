package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.serializer.GameMessage
import dev.mcd.chess.serializer.MessageType
import dev.mcd.chess.serializer.sessionInfoMessage
import java.lang.Exception

interface CommandHandler {
    suspend fun handleCommand(
        session: GameSession,
        command: String,
        userSide: Side,
    ): GameMessage?
}

class CommandHandlerImpl(
    private val sessionManager: SessionManager
) : CommandHandler {
    override suspend fun handleCommand(
        session: GameSession,
        command: String,
        userSide: Side,
    ): GameMessage? {
        println("command: $command")
        var newSession = session

        try {
            if (command == "resign") {
                sessionManager.update(session.copy(state = State.resigned(userSide)))
            } else if (userSide == session.board.sideToMove) {
                val board = session.board
                val move = Move(command, session.board.sideToMove)
                if (board.doMove(move, true)) {
                    if (board.isMated) {
                        newSession = session.copy(state = State.checkmated(userSide.flip()))
                    } else if (board.isDraw) {
                        newSession = session.copy(state = State.DRAW)
                    }
                    println("new fen=${board.fen}")
                } else {
                    return GameMessage(MessageType.ErrorInvalidMove)
                }
                sessionManager.update(newSession)
            } else {
                return GameMessage(MessageType.ErrorNotUsersMove)
            }
        } catch(e: Exception) {
            println("handling command: $e")
            e.printStackTrace()
        }
        return null
    }
}
