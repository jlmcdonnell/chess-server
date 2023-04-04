package dev.mcd.chess.di

import dev.mcd.chess.auth.LiveUsers
import dev.mcd.chess.auth.LiveUsersImpl
import dev.mcd.chess.game.CommandHandler
import dev.mcd.chess.game.CommandHandlerImpl
import dev.mcd.chess.game.GameFactory
import dev.mcd.chess.game.GameFactoryImpl
import dev.mcd.chess.game.GameManager
import dev.mcd.chess.game.Lobby
import dev.mcd.chess.game.LobbyImpl
import dev.mcd.chess.game.GameManagerImpl
import dev.mcd.chess.puzzles.PuzzleRepository
import dev.mcd.chess.puzzles.PuzzleRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<Lobby> { LobbyImpl(get(), get()) }
    single<LiveUsers> { LiveUsersImpl() }
    single<GameManager> { GameManagerImpl() }
    single<CommandHandler> { CommandHandlerImpl(get()) }
    single<GameFactory> { GameFactoryImpl() }
    single<PuzzleRepository> { PuzzleRepositoryImpl() }
}
