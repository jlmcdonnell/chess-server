package dev.mcd.chess.auth

interface LiveUsers {
    fun new(): UserId
    fun exists(id: String): Boolean
    fun add(id: String)
}

class LiveUsersImpl : LiveUsers {
    private var users = mutableListOf<String>()

    override fun add(id: String) {
        users += id
    }

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
