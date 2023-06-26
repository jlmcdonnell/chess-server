package dev.mcd.chess.puzzles

import dev.mcd.chess.puzzles.db.Puzzles
import dev.mcd.chess.puzzles.db.maxRatedPuzzle
import dev.mcd.chess.puzzles.db.minRatedPuzzle
import dev.mcd.chess.puzzles.db.toPuzzle
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max
import kotlin.math.min

class PuzzleRepositoryImpl : PuzzleRepository {

    private lateinit var allPuzzleRange: IntRange

    init {
        transaction {
            allPuzzleRange = Puzzles.minRatedPuzzle()..Puzzles.maxRatedPuzzle()
        }
    }

    override suspend fun getRandomPuzzle(ratingMin: Int, ratingMax: Int): Puzzle {
        val adjustedMin = max(allPuzzleRange.first, ratingMin)
        val adjustedMax = max(adjustedMin, min(allPuzzleRange.last, ratingMax))

        return transaction {
            Puzzles.selectAll()
                .andWhere { Puzzles.rating greaterEq adjustedMin }
                .andWhere { Puzzles.rating lessEq adjustedMax }
                .orderBy(Random())
                .limit(1)
                .first()
                .toPuzzle()
        }
    }

    override suspend fun getPuzzle(id: String): Puzzle {
        return transaction {
            Puzzles.select { Puzzles.id eq id }
                .first()
                .toPuzzle()
        }
    }
}
