package dev.mcd.chess.serializer

import dev.mcd.chess.puzzles.Puzzle
import kotlinx.serialization.Serializable

@Serializable
data class PuzzleSerializer(
    val puzzleId: String,
    val fen: String,
    val moves: List<String>,
    val rating: Int,
    val themes: List<String>,
)

fun Puzzle.serializer() = PuzzleSerializer(
    puzzleId = puzzleId,
    fen = fen,
    moves = moves,
    rating = rating,
    themes = themes,
)
