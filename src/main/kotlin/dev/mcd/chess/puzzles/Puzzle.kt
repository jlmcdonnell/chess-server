package dev.mcd.chess.puzzles

data class Puzzle(
    val puzzleId: String,
    val fen: String,
    val moves: List<String>,
    val rating: Int,
    val themes: List<String>,
)
