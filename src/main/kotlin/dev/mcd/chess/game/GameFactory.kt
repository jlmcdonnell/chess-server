package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.game.Event
import com.github.bhlangonijr.chesslib.game.Game
import com.github.bhlangonijr.chesslib.game.GenericPlayer
import com.github.bhlangonijr.chesslib.game.Round
import com.github.bhlangonijr.chesslib.move.MoveList
import dev.mcd.chess.auth.UserId
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID

interface GameFactory {
    suspend fun generateGame(playerWhite: UserId, playerBlack: UserId): GameSession
    suspend fun generateCustomGame(initialFen: String, playerWhite: UserId, playerBlack: UserId): GameSession
}

class GameFactoryImpl : GameFactory {

    override suspend fun generateGame(playerWhite: UserId, playerBlack: UserId): GameSession {
        return generateCustomGame(Constants.startStandardFENPosition, playerWhite, playerBlack)
    }

    override suspend fun generateCustomGame(initialFen: String, playerWhite: UserId, playerBlack: UserId): GameSession {
        val sessionId = UUID.randomUUID().toString()
        val event = Event()
        event.startDate = OffsetDateTime.now(ZoneId.of(ZoneOffset.UTC.id)).toString()
        val round = Round(event)
        val game = Game(sessionId, round).apply {
            whitePlayer = GenericPlayer(playerWhite, playerWhite)
            blackPlayer = GenericPlayer(playerBlack, playerBlack)
            board = Board()
            fen = initialFen
            moveText = StringBuilder()
            halfMoves = MoveList(fen)
            board.loadFromFen(fen)
            gotoLast()
        }

        return GameSession(
            id = sessionId,
            game = game,
        )
    }
}
