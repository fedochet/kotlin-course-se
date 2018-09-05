package ru.hse.spb

/**
 * Solution for 113A problem ([link](http://codeforces.com/problemset/problem/113/A)).
 */

private enum class Gender {
    MALE, FEMALE
}

private enum class WordType {
    VERB, NOUN, ADJECTIVE
}

private data class Word(val type: WordType, val gender: Gender) {

    companion object {
        fun parseWord(word: String): Word? =
            when {
                word.endsWith("lios") -> Word(WordType.ADJECTIVE, Gender.MALE)
                word.endsWith("liala") -> Word(WordType.ADJECTIVE, Gender.FEMALE)

                word.endsWith("etr") -> Word(WordType.NOUN, Gender.MALE)
                word.endsWith("etra") -> Word(WordType.NOUN, Gender.FEMALE)

                word.endsWith("initis") -> Word(WordType.VERB, Gender.MALE)
                word.endsWith("inites") -> Word(WordType.VERB, Gender.FEMALE)

                else -> null
            }
    }

}

private fun genderIsSame(validWords: List<Word>): Boolean {
    val expectedGender = validWords.firstOrNull()?.gender ?: return false
    return validWords.all { it.gender === expectedGender }
}

private fun structureIsCorrect(validWords: List<Word>): Boolean {
    val firstNounIndex = validWords.indexOfFirst { it.type === WordType.NOUN }
    if (firstNounIndex == -1) return false

    val adjectives = validWords.subList(0, firstNounIndex)
    val verbs = validWords.subList(firstNounIndex + 1, validWords.size)

    return adjectives.all { it.type === WordType.ADJECTIVE }
        && verbs.all { it.type === WordType.VERB }
}

private fun isPhrase(words: List<Word?>): Boolean {
    val validWords = words.filterNotNull()

    return validWords.size == words.size
        && genderIsSame(validWords)
        && structureIsCorrect(validWords)
}

fun isCorrectSentence(words: List<String>): Boolean {
    val parsedWords = words.map { Word.parseWord(it) }

    return parsedWords.singleOrNull() != null
        || isPhrase(parsedWords)
}

fun main(args: Array<String>) {
    val words = readLine()?.split(' ') ?: return

    println(if (isCorrectSentence(words)) "YES" else "NO")
}
