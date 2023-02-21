package dev.mcd.chess.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(io.ktor.server.plugins.statuspages.StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(status = HttpStatusCode.InternalServerError, message = "500; That's more than 200.")
        }
    }
}
