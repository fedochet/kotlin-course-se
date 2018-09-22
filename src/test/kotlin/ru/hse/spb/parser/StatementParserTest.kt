package ru.hse.spb.parser

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.hse.spb.funlang.Literal
import ru.hse.spb.funlang.Statement
import ru.hse.spb.funlang.VarDeclaration

class StatementParserTest {

    @Test
    fun `variable declaration without initializer is parsed`() {
        assertThat(parse("var i")).isEqualTo(VarDeclaration("i", null))
    }

    @Test
    fun `variable declaration with initializer is parsed`() {
        assertThat(parse("var i = 1")).isEqualTo(VarDeclaration("i", Literal(1)))
    }

    private fun parse(code: String): Statement {
        val lexer = FunLangLexer(CharStreams.fromString(code))
        val parser = FunLangParser(BufferedTokenStream(lexer))

        return StatementParser.visit(parser.parse())
    }

}