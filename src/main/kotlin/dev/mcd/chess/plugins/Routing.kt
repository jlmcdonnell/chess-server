package dev.mcd.chess.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.mcd.chess.Environment
import dev.mcd.chess.auth.AuthResponse
import dev.mcd.chess.auth.user.LiveUsers
import dev.mcd.chess.auth.user.UserId
import dev.mcd.chess.game.Lobby
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import org.koin.ktor.ext.get
import java.nio.charset.Charset

fun Application.configureRouting() {
    val users = get<LiveUsers>()
    val lobby = get<Lobby>()

    routing {
        get("/debugversion") {
            call.respond("0")
        }
        post("/generate_id") {
            val id = users.new()

            val token = JWT.create()
                .withJWTId(id)
                .withAudience(Environment.jwtAudience)
                .withIssuer(Environment.jwtDomain)
                .sign(Algorithm.HMAC512(Environment.secret))

            call.respond(AuthResponse(token = token, userId = id))
        }
        authenticate {
            route("/game") {
                post("/find") {
                    val userId = call.authentication.principal<JWTPrincipal>()!!.jwtId as UserId
                    call.respond(lobby.awaitSession(userId))
                }
                webSocket("/join") {
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        send(Frame.Text("And how does ${frame.data.toString(Charset.defaultCharset())} make you feel?"))
                    }
                }
            }
        }
    }
}
