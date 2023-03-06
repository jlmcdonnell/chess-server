package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Board
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
    suspend fun generateGame(player1: UserId, player2: UserId): GameSession
}

class GameFactoryImpl : GameFactory {

    override suspend fun generateGame(playerWhite: UserId, playerBlack: UserId): GameSession {
        val sessionId = UUID.randomUUID().toString()
        val event = Event()
        event.startDate = OffsetDateTime.now(ZoneId.of(ZoneOffset.UTC.id)).toString()
        val round = Round(event)
        val game = Game(sessionId, round).apply {
            whitePlayer = GenericPlayer(playerWhite, playerWhite)
            blackPlayer = GenericPlayer(playerBlack, playerBlack)
            board = Board()
            moveText = StringBuilder()
            halfMoves = MoveList()
            gotoLast()
        }

        return GameSession(
            id = sessionId,
            game = game,
        )
    }
}
