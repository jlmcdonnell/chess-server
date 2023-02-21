package dev.mcd.chess.auth

import dev.mcd.chess.auth.user.UserId
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: UserId,
)
