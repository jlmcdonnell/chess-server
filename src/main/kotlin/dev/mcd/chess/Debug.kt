package dev.mcd.chess

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.auth.LiveUsers
import dev.mcd.chess.game.GameManager
import dev.mcd.chess.game.GameSession
import dev.mcd.chess.game.SessionState
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.get

fun Application.configureDebugEnvironment() {
    if (System.getenv("DEBUG") != null) {
        val sessionManager = get<GameManager>()
        val users = get<LiveUsers>()

        users.add("user1")
        users.add("user2")

        runBlocking {
            sessionManager.add(
                GameSession(
                    sessionId = "debug",
                    playerWhite = "user1",
                    playerBlack = "user2",
                    board = Board(),
                    state = SessionState.STARTED,
                )
            )
        }

        val tokens = hashMapOf<String, String>()

        listOf("user1", "user2").forEach { id ->
            tokens[id] = JWT.create()
                .withJWTId(id)
                .withAudience(Environment.jwtAudience)
                .withIssuer(Environment.jwtDomain)
                .sign(Algorithm.HMAC512(Environment.secret))
        }

        routing {
            get("/debuginfo") {
                call.respond(
                    buildMap {
                        put("user1", tokens["user1"]!!)
                        put("user2", tokens["user2"]!!)
                    }
                )
            }
        }
    }
}
