package dev.mcd.chess.di

import dev.mcd.chess.auth.LiveUsers
import dev.mcd.chess.auth.LiveUsersImpl
import dev.mcd.chess.game.CommandHandler
import dev.mcd.chess.game.CommandHandlerImpl
import dev.mcd.chess.game.Lobby
import dev.mcd.chess.game.LobbyImpl
import dev.mcd.chess.game.GameManager
import dev.mcd.chess.game.SessionManagerImpl
import org.koin.dsl.module

val appModule = module {
    single<Lobby> { LobbyImpl(get()) }
    single<LiveUsers> { LiveUsersImpl() }
    single<GameManager> { SessionManagerImpl() }
    single<CommandHandler> { CommandHandlerImpl(get()) }
}
