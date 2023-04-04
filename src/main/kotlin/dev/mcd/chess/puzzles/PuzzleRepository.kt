package dev.mcd.chess.puzzles

interface PuzzleRepository {
    suspend fun getRandomPuzzle(): Puzzle
}
