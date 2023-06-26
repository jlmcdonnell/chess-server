package dev.mcd.chess.routing

import dev.mcd.chess.puzzles.PuzzleRepository
import dev.mcd.chess.serializer.serializer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Routing.puzzles() {
    val puzzleRepo = get<PuzzleRepository>()

    get("/puzzles/random") {
        val ratingMin = call.request.queryParameters["ratingMin"]?.toInt() ?: 0
        val ratingMax = call.request.queryParameters["ratingMax"]?.toInt() ?: 0
        val puzzle = puzzleRepo.getRandomPuzzle(ratingMin, ratingMax)
        call.respond(puzzle.serializer())
    }

    get("/puzzles/id/{id}") {
        val id = call.parameters["id"] ?: throw BadRequestException("Invalid puzzle ID")
        val puzzle = puzzleRepo.getPuzzle(id)
        call.respond(puzzle.serializer())
    }
}
