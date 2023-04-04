package dev.mcd.chess.puzzles

interface PuzzleRepository {
    suspend fun getPuzzle(id: String): Puzzle
    suspend fun getRandomPuzzle(): Puzzle
}
