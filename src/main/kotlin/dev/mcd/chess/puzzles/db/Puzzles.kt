package dev.mcd.chess.puzzles.db

import org.jetbrains.exposed.dao.id.IdTable

object Puzzles : IdTable<String>() {
    override val id = varchar("puzzleId", 255).entityId()
    val fen = text("fen")
    val moves = text("moves")
    val rating = integer("rating")
    val themes = text("themes")
}
