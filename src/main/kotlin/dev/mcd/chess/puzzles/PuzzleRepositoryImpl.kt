package dev.mcd.chess.puzzles

import dev.mcd.chess.puzzles.db.Puzzles
import dev.mcd.chess.puzzles.db.toPuzzle
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max
import kotlin.math.min

class PuzzleRepositoryImpl : PuzzleRepository {

    private val allPuzzleRange: IntRange

    init {
        val minRange = Puzzles.selectAll()
            .orderBy(Puzzles.rating, SortOrder.DESC)
            .limit(1)
            .first()
            .toPuzzle().rating

        val maxRange = Puzzles.selectAll()
            .orderBy(Puzzles.rating, SortOrder.ASC)
            .limit(1)
            .first()
            .toPuzzle().rating

        allPuzzleRange = minRange..maxRange
    }

    override suspend fun getRandomPuzzle(ratingMin: Int, ratingMax: Int): Puzzle {
        val adjustedMin = max(allPuzzleRange.first, ratingMin)
        val adjustedMax = min(allPuzzleRange.last, ratingMax)

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
