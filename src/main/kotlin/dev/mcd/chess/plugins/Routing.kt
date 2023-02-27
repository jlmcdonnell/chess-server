package dev.mcd.chess.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.Environment
import dev.mcd.chess.auth.LiveUsers
import dev.mcd.chess.auth.UserId
import dev.mcd.chess.game.CommandHandler
import dev.mcd.chess.game.Lobby
import dev.mcd.chess.game.SessionManager
import dev.mcd.chess.serializer.AuthSerializer
import dev.mcd.chess.serializer.sessionInfoMessage
import dev.mcd.chess.serializer.sessionInfoSerializer
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    val users = get<LiveUsers>()
    val lobby = get<Lobby>()
    val sessionManager = get<SessionManager>()
    val commandHandler = get<CommandHandler>()

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

            call.respond(AuthSerializer(token = token, userId = id))
        }
        authenticate {
            route("/game") {

                get("/session/{sessionId}") {
                    val sessionId = call.parameters["sessionId"] ?: throw BadRequestException("No session provided")
                    val session = sessionManager.session(id = sessionId)
                    call.respond(session.sessionInfoSerializer())
                }
                post("/find") {
                    val userId = call.authentication.principal<JWTPrincipal>()!!.jwtId as UserId
                    val sessionId = lobby.awaitSession(userId)
                    val session = sessionManager.session(sessionId)
                    call.respond(session.sessionInfoSerializer())
                }
            }

            webSocket("/game/join/{sessionId}") {

                val sessionId = call.parameters["sessionId"] ?: throw BadRequestException("No session provided")
                var session = sessionManager.session(id = sessionId)
                val userId = call.authentication.principal<JWTPrincipal>()!!.jwtId!!
                val userSide = if (userId == session.playerWhite) Side.WHITE else Side.BLACK

                launch {
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val command = frame.readText().trim()
                        val message = commandHandler.handleCommand(session, command, userSide)
                        message?.let { sendSerialized(message) }
                    }
                    val closeReason = closeReason.await()
                    println("Closed: $closeReason")
                }
                sessionManager.updates(sessionId).collectLatest {
                    session = it
                    sendSerialized(session.sessionInfoMessage())
                }
            }
        }
    }
}
