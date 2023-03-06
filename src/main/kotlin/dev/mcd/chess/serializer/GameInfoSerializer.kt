package dev.mcd.chess.serializer

import dev.mcd.chess.game.GameId
import dev.mcd.chess.game.GameSession
import dev.mcd.chess.game.toPgn
import kotlinx.serialization.Serializable
import java.util.Base64

@Serializable
data class GameInfoSerializer(
    val id: GameId,
    val pgn: String,
)

fun GameSession.gameInfoSerializer(): GameInfoSerializer {
    val pgn = toPgn().let {
        Base64.getEncoder().withoutPadding().encodeToString(it.toByteArray())
    }
    return GameInfoSerializer(
        id = id,
        pgn = pgn,
    )
}

