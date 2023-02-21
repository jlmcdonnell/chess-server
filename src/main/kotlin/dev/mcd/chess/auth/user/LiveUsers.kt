package dev.mcd.chess.auth.user

import dev.mcd.chess.game.SessionManager

interface LiveUsers {
    fun new(): UserId
    fun exists(id: String): Boolean
}

class LiveUsersImpl : LiveUsers {
    private var users = mutableListOf<String>()

    override fun new(): UserId {
        var id: String
        do {
            id = "${WordList.random()}-${WordList.random()}"
        } while (exists(id))
        users += id
        return id
    }

    override fun exists(id: String) = id in users
}
