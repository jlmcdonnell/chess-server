package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.pgn.PgnIterator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class GameFactoryImplTest {

    @Test
    fun `GameFactory configures GameSession correctly to provide re-parseable PGN`() {
        runBlocking {
            val factory = GameFactoryImpl()
            val fen = "3k4/8/3K3R/8/8/8/8/8 w - - 0 1"
            val session = factory.generateCustomGame(fen, "p1", "p2")
            val pgnString = session.toPgn()
            PgnIterator(pgnString.lineSequence().iterator()).first()
        }
    }
}
