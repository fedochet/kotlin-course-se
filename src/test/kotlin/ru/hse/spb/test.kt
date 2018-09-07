package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TestSource {
    @Test
    fun `single correct word is accepted`() {
        assertTrue(isCorrectSentence("petr".split(' ')))
    }

    @Test
    fun `correct phrase is accepted`() {
        assertTrue(isCorrectSentence("nataliala kataliala vetra feinites".split(' ')))
    }

    @Test
    fun `incorrect phrase is rejetced`() {
        assertFalse(isCorrectSentence("etis atis animatis etis atis amatis".split(' ')))
    }
}