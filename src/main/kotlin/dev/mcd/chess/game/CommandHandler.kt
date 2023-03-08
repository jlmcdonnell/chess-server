package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.serializer.GameMessage
import dev.mcd.chess.serializer.MessageType
import dev.mcd.chess.serializer.gameStateMessage

interface CommandHandler {
    suspend fun handleCommand(
        session: GameSession,
        command: String,
        userSide: Side,
    ): CommandResult
}

sealed interface CommandResult {
    object NoReply : CommandResult
    data class MessageReply(val message: GameMessage) : CommandResult
}

class CommandHandlerImpl(
    private val gameManager: GameManager
) : CommandHandler {
    override suspend fun handleCommand(
        session: GameSession,
        command: String,
        userSide: Side,
    ): CommandResult {
        return if (session.isTerminated) {
            CommandResult.NoReply
        } else if (command == "resign") {
            session.resignForSide(userSide)
            gameManager.notifyUpdate(session)
            CommandResult.NoReply
        } else if (command == "state") {
            CommandResult.MessageReply(session.gameStateMessage())
        } else if (userSide == session.game.board.sideToMove) {
            val board = session.game.board

            val move = Move(command, session.game.board.sideToMove)
            if (board.doMove(move, true)) {
                session.game.halfMoves.add(move)
                val gameTerminated = session.updateResultAndTermination(userSide)
                if (gameTerminated) {
                    gameManager.notifyUpdate(session)
                }
                CommandResult.NoReply
            } else {
                CommandResult.MessageReply(GameMessage(MessageType.ErrorInvalidMove))
            }
        } else {
            CommandResult.MessageReply(GameMessage(MessageType.ErrorNotUsersMove))
        }
    }
}
