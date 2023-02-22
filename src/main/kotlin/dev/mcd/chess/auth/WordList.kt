package dev.mcd.chess.auth

import java.security.SecureRandom

object WordList {
    private val words by lazy {
        WordList::class.java.classLoader.getResource("words")
            .openStream()
            .reader()
            .readLines()
    }

    fun random() = words[SecureRandom().nextInt(words.lastIndex)]
}
