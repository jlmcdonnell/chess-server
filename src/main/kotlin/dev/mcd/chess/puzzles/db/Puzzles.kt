package dev.mcd.chess.puzzles.db

import dev.mcd.chess.puzzles.Puzzle
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll

object Puzzles : IdTable<String>() {
    override val id = varchar("puzzleId", 255).entityId()
    val fen = text("fen")
    val moves = text("moves")
    val rating = integer("rating")
    val themes = text("themes")
}

fun Puzzles.maxRatedPuzzle(): Int {
    return Puzzles.selectAll()
        .orderBy(rating, SortOrder.DESC)
        .limit(1)
        .first()[rating]
}

fun Puzzles.minRatedPuzzle(): Int {
    return Puzzles.selectAll()
        .orderBy(rating, SortOrder.ASC)
        .limit(1)
        .first()[rating]
}

fun ResultRow.toPuzzle() = Puzzle(
    puzzleId = this[Puzzles.id].value,
    fen = this[Puzzles.fen],
    moves = this[Puzzles.moves].split(' '),
    rating = this[Puzzles.rating],
    themes = this[Puzzles.themes].split(' '),
)

