package dev.mcd.chess.serializer

import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfoSerializer(
    val inLobby: Int,
)
