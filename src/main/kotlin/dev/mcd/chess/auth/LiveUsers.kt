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
            val word1 = WordList.random().replaceFirstChar { it.uppercaseChar() }
            val word2 = WordList.random().replaceFirstChar { it.uppercaseChar() }
            id = "$word1$word2"
        } while (exists(id))
        users += id
        return id
    }

    override fun exists(id: String) = id in users
}
