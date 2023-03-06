package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.move.MoveList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.lang.StringBuilder

class GameFactoryImplTest {

    @Test
    fun toPgn() = runBlocking {
        val factory = GameFactoryImpl()
        val session = factory.generateGame("p1", "p2")
        with (session.game) {
            moveText = StringBuilder()
            halfMoves = MoveList(Constants.startStandardFENPosition)
        }
        session.game.gotoLast()
        println(session.toPgn())
    }
}
