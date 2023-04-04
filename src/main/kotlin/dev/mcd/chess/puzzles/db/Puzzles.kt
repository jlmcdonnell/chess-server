package dev.mcd.chess.puzzles.db

import dev.mcd.chess.puzzles.Puzzle
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow

object Puzzles : IdTable<String>() {
    override val id = varchar("puzzleId", 255).entityId()
    val fen = text("fen")
    val moves = text("moves")
    val rating = integer("rating")
    val themes = text("themes")
}

fun ResultRow.toPuzzle() = Puzzle(
    puzzleId = this[Puzzles.id].value,
    fen = this[Puzzles.fen],
    moves = this[Puzzles.moves].split(' '),
    rating = this[Puzzles.rating],
    themes = this[Puzzles.themes].split(' '),
)
