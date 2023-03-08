package dev.mcd.chess.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.GameResult
import com.github.bhlangonijr.chesslib.pgn.PgnIterator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommandHandlerImplTest {

    private lateinit var sut: CommandHandlerImpl
    private lateinit var factory: GameFactory
    private lateinit var manager: GameManager

    @BeforeEach
    fun setUp() {
        factory = GameFactoryImpl()
        manager = GameManagerImpl()
    }

    @Test
    fun `Mate leads to termination and result state change`() {
        runBlocking {
            val game = factory.generateCustomGame("3k4/8/3K3R/8/8/8/8/8 w - - 0 1", "w", "b")
            manager.add(game)

            sut = CommandHandlerImpl(manager)
            sut.handleCommand(game, "h6h8", Side.WHITE)

            with(manager.getGame(game.id)) {
                assertTrue(isTerminated)
                val pgn = toPgn()
                PgnIterator(pgn.lineSequence().iterator()).first()
            }
        }
    }

    @Test
    fun `Resignation leads to termination and result state change`() {
        runBlocking {
            val game = factory.generateGame("w", "b")
            manager.add(game)

            sut = CommandHandlerImpl(manager)
            sut.handleCommand(game, "resign", Side.WHITE)

            with(manager.getGame(game.id)) {
                assertTrue(isTerminated)
                assertEquals(GameResult.BLACK_WON, game.game.result)
            }
        }
    }

    @Test
    fun `Parse back to PGN after termination`() {
        runBlocking {
            val game = factory.generateCustomGame("3k4/8/3K3R/8/8/8/8/8 w - - 0 1", "w", "b")
            manager.add(game)

            sut = CommandHandlerImpl(manager)
            sut.handleCommand(game, "h6h8", Side.WHITE)

            val updated = manager.getGame(game.id)
            assertTrue(updated.isTerminated)

            val pgn = updated.toPgn()
            PgnIterator(pgn.lineSequence().iterator()).first()
        }
    }
}
