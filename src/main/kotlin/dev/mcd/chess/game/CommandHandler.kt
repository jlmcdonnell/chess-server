package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.serializer.GameMessage
import dev.mcd.chess.serializer.MessageType
import dev.mcd.chess.serializer.moveHistoryMessage
import dev.mcd.chess.serializer.moveMessage

interface CommandHandler {
    suspend fun handleCommand(
        session: GameSession,
        command: String,
        userSide: Side,
    ): GameMessage?
}

class CommandHandlerImpl(
    private val sessionManager: GameManager
) : CommandHandler {
    override suspend fun handleCommand(
        session: GameSession,
        command: String,
        userSide: Side,
    ): GameMessage? {
        println("command: $command")

        try {
            if (session.state != SessionState.STARTED) {
                return GameMessage(MessageType.ErrorGameTerminated)
            } else if (command == "resign") {
                sessionManager.update(session.copy(state = SessionState.resigned(userSide)))
            } else if (command == "history") {
                return session.moveHistoryMessage()
            } else if (userSide == session.board.sideToMove) {
                val board = session.board
                val move = Move(command, session.board.sideToMove)
                if (board.doMove(move, true)) {
                    val newState = if (board.isMated) {
                        SessionState.checkmated(userSide.flip())
                    } else if (board.isDraw) {
                        SessionState.DRAW
                    } else null
                    newState?.let {
                        sessionManager.update(session.copy(state = newState))
                    }
                } else {
                    return GameMessage(MessageType.ErrorInvalidMove)
                }
            } else {
                return GameMessage(MessageType.ErrorNotUsersMove)
            }
        } catch (e: Exception) {
            println("handling command: $e")
            e.printStackTrace()
        }
        return null
    }
}
