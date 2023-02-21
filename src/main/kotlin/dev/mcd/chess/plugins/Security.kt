package dev.mcd.chess.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.mcd.chess.Environment
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity() {

    authentication {
        jwt {
            verifier {
                JWT.require(Algorithm.HMAC512(Environment.secret))
                    .withAudience(Environment.jwtAudience)
                    .withIssuer(Environment.jwtDomain)
                    .build()
            }
            validate { credential ->
                JWTPrincipal(credential.payload).takeIf { credential.payload.audience.contains(Environment.jwtAudience) }
            }
            realm = Environment.jwtAudience
        }
    }
}
