package dev.mcd.chess.routing

import dev.mcd.chess.puzzles.PuzzleRepository
import dev.mcd.chess.serializer.serializer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Routing.puzzles() {
    val puzzleRepo = get<PuzzleRepository>()

    get("/puzzles") {
        val puzzle = puzzleRepo.getRandomPuzzle()
        call.respond(puzzle.serializer())
    }
}
