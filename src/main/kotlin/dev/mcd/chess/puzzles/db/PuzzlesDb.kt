package dev.mcd.chess.puzzles.db

import dev.mcd.chess.Environment
import org.jetbrains.exposed.sql.Database

fun configurePuzzlesDatabase() {
    val dbPath = Environment.puzzlesDbPath

    // Configure Exposed Sqlite DB connection for path
    Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")

}
