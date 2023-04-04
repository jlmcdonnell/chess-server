package dev.mcd.chess.puzzles

import dev.mcd.chess.puzzles.db.Puzzles
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PuzzleRepositoryImpl : PuzzleRepository {

    override suspend fun getRandomPuzzle(): Puzzle {
        return transaction {
            Puzzles.selectAll()
                .orderBy(Random())
                .limit(1)
                .first()
                .let {
                    Puzzle(
                        puzzleId = it[Puzzles.id].value,
                        fen = it[Puzzles.fen],
                        moves = it[Puzzles.moves].split(' '),
                        rating = it[Puzzles.rating],
                        themes = it[Puzzles.themes].split(' '),
                    )
                }
        }
    }
}
