package dev.mcd.chess.puzzles

import dev.mcd.chess.puzzles.db.Puzzles
import dev.mcd.chess.puzzles.db.toPuzzle
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PuzzleRepositoryImpl : PuzzleRepository {

    override suspend fun getRandomPuzzle(): Puzzle {
        return transaction {
            Puzzles.selectAll()
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
