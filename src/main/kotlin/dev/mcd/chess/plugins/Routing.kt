package dev.mcd.chess.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respondText(text = "500; That's more than 200.")
        }
    }
}
