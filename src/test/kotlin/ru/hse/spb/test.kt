package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TestSource {
    @Test
    fun testExamples() {
        assertTrue(isCorrectSentence("petr".split(' ')))
        assertFalse(isCorrectSentence("etis atis animatis etis atis amatis".split(' ')))
        assertTrue(isCorrectSentence("nataliala kataliala vetra feinites".split(' ')))
    }
}