package dev.mcd.chess

object Environment {
    val secret by lazy { System.getenv("SECRET") }
    val jwtDomain by lazy { System.getenv("JWT_DOMAIN") }
    val jwtAudience = "You."
    val puzzlesDbPath by lazy { System.getenv("PUZZLES_DB_PATH") }
}
