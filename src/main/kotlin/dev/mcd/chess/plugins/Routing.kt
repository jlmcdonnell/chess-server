package dev.mcd.chess.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.Environment
import dev.mcd.chess.auth.LiveUsers
import dev.mcd.chess.game.CommandHandler
import dev.mcd.chess.game.CommandResult
import dev.mcd.chess.game.GameManager
import dev.mcd.chess.game.Lobby
import dev.mcd.chess.game.isTerminated
import dev.mcd.chess.serializer.AuthSerializer
import dev.mcd.chess.serializer.LobbyInfoSerializer
import dev.mcd.chess.serializer.gameInfoSerializer
import dev.mcd.chess.serializer.gameStateMessage
import dev.mcd.chess.serializer.moveMessage
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
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.ext.get
import java.time.Duration

fun Application.configureRouting() {
    val users = get<LiveUsers>()
    val lobby = get<Lobby>()
    val gameManager = get<GameManager>()
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
        get("/game/lobby") {
            call.respond(LobbyInfoSerializer(lobby.count()))
        }

        authenticate {
            route("/game") {
                get("/id/{sessionId}") {
                    val sessionId = call.parameters["sessionId"] ?: throw BadRequestException("No session provided")
                    val session = gameManager.getGame(id = sessionId)
                    call.respond(session.gameInfoSerializer())
                }
                get("/user") {
                    val userId = call.authentication.principal<JWTPrincipal>()!!.jwtId!!
                    val games = gameManager.getActiveGamesForUser(userId)
                        .filter { !it.isTerminated }
                        .map { it.gameInfoSerializer() }
                    call.respond(games)
                }
            }

            webSocket("/game/find") {
                val userId = call.authentication.principal<JWTPrincipal>()!!.jwtId!!
                timeout = Duration.ofSeconds(15)
                launch {
                    try {
                        val sessionId = lobby.awaitSession(userId)
                        val session = gameManager.getGame(sessionId)
                        sendSerialized(session.gameStateMessage())
                        close(CloseReason(CloseReason.Codes.NORMAL, "Joined Session"))
                    } finally {
                        lobby.leave(userId)
                    }
                }
                closeReason.await()
                lobby.leave(userId)
            }

            webSocket("/game/join/{sessionId}") {
                val id = call.parameters["sessionId"] ?: throw BadRequestException("No session provided")
                var session = gameManager.getGame(id = id)
                val userId = call.authentication.principal<JWTPrincipal>()!!.jwtId!!
                val userSide = if (userId == session.game.whitePlayer.id) Side.WHITE else Side.BLACK

                launch {
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val command = frame.readText().trim()
                        println("Command: $command")
                        val result = commandHandler.handleCommand(session, command, userSide)
                        if (result is CommandResult.MessageReply) {
                            println("Reply: ${result.message.message}")
                            sendSerialized(result.message)
                        } else {
                            println("(No Reply)")
                        }
                    }
                }

                val gameUpdateJob = launch(Dispatchers.Default) {
                    gameManager.getGameUpdates(id).collectLatest {
                        session = it
                        sendSerialized(session.gameStateMessage())
                    }
                }

                val moveJob = launch(Dispatchers.Default) {
                    gameManager.getMoves(id).collectLatest {
                        println("Sending $it to $userId (active=$isActive) close=$closeReason")
                        sendSerialized(it.toString().moveMessage(session.game.board.moveCounter))
                    }
                }

                val closeReason = closeReason.await()
                println("Closed ($closeReason)")

                gameUpdateJob.cancel()
                moveJob.cancel()
            }
        }
    }
}
