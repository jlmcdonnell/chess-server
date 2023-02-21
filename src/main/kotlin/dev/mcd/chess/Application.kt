package dev.mcd.chess

import dev.mcd.chess.plugins.configureRouting
import dev.mcd.chess.plugins.configureSecurity
import dev.mcd.chess.plugins.configureSerialization
import dev.mcd.chess.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureSockets()
    configureRouting()
}
