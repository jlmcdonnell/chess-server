package dev.mcd.chess.serializer

import kotlinx.serialization.Serializable

@Serializable
data class MoveSerializer(
    val move: String,
    val count: Int,
)
