package dev.mcd.chess.serializer

import dev.mcd.chess.auth.UserId
import kotlinx.serialization.Serializable

@Serializable
data class AuthSerializer(
    val token: String,
    val userId: UserId,
)
