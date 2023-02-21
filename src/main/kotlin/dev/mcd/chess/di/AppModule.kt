package dev.mcd.chess.di

import dev.mcd.chess.auth.user.LiveUsers
import dev.mcd.chess.auth.user.LiveUsersImpl
import dev.mcd.chess.game.Lobby
import dev.mcd.chess.game.LobbyImpl
import dev.mcd.chess.game.SessionManager
import dev.mcd.chess.game.SessionManagerImpl
import org.koin.dsl.module

val appModule = module {
    single<Lobby> { LobbyImpl(get()) }
    single<LiveUsers> { LiveUsersImpl() }
    single<SessionManager> { SessionManagerImpl() }
}
