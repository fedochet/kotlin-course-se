package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.assertj.core.api.Assertions.*
import org.junit.Test
import ru.hse.spb.interpreter.Context
import ru.hse.spb.interpreter.evalBlock
import ru.hse.spb.parser.BlockParser
import ru.hse.spb.parser.FunLangLexer
import ru.hse.spb.parser.FunLangParser

class IntegrationTest {
    val stdout = mutableListOf<String>()

    val TEST_RUNTIME = Context.empty().apply {
        addBuiltInFunction("println") { args ->
            stdout.add(args.joinToString(separator = " "))
            0
        }
    }

    @Test
    fun `simple programm is executed correctly`() {
        val program = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        assertThat(eval(program)).isEqualTo(0)
        assertThat(stdout).containsExactly("0")
    }

    @Test
    fun `fib programm`() {
        val program = """
            fun fib(n) {
                if (n <: 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }

            var i = 1
            while (i <: 5) {
                println(i, fib(i))
                i = i + 1
            }
        """.trimIndent()

        assertThat(eval(program)).isEqualTo(0)
        assertThat(stdout).containsExactly(
            "1 1",
            "2 2",
            "3 3",
            "4 5",
            "5 8"
        )
    }

    @Test
    fun `inner functions`() {
        val program = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }

                return bar(1)
            }
            // some other comment
            println(foo(41)) // prints 42
        """.trimIndent()

        assertThat(eval(program)).isEqualTo(0)
        assertThat(stdout).containsExactly("42")
    }

    fun eval(programm: String): Int {
        val lexer = FunLangLexer(CharStreams.fromString(programm))
        val parser = FunLangParser(BufferedTokenStream(lexer))
        val block = BlockParser.visit(parser.parse())

        return evalBlock(block, TEST_RUNTIME)
    }
}
