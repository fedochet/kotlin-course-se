package ru.hse.spb.parser

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.hse.spb.funlang.*

class StatementParserTest {

    @Test
    fun `variable declaration without initializer is parsed`() {
        assertThat(parse("var i")).isEqualTo(VarDeclaration("i", null))
    }

    @Test
    fun `variable declaration with initializer is parsed`() {
        assertThat(parse("var i = 1")).isEqualTo(VarDeclaration("i", Literal(1)))
    }

    @Test
    fun `function declaration without arguments and body is parsed`() {
        assertThat(parse("fun funName() {}"))
            .isEqualTo(FunctionDeclaration("funName", emptyList(), Block(emptyList())))
    }

    @Test
    fun `function declaration with arguments and body is parsed`() {
        assertThat(parse("""
            fun funName(a, b, c) {
                var i = 10
                print(i)
            }
            """.trimIndent())).isEqualTo(
            FunctionDeclaration(
                "funName", listOf("a", "b", "c"),
                Block(listOf(
                    VarDeclaration("i", Literal(10)),
                    FunctionCall("print", listOf(Ident("i")))
                ))
            )
        )
    }

    @Test
    fun `if statement without else is parsed`() {
        assertThat(parse("""
            if (i) {
                print(i)
            }
            """.trimIndent())).isEqualTo(
            If(Ident("i"), Block(listOf(
                FunctionCall("print", listOf(Ident("i"))))
            ), null)
        )
    }

    @Test
    fun `if statement with else is parsed`() {
        assertThat(parse("""
            if (i) {
                thenFun(i)
            } else {
                elseFun(i)
            }
            """.trimIndent())).isEqualTo(
            If(Ident("i"), Block(listOf(
                FunctionCall("thenFun", listOf(Ident("i"))))
            ), Block(listOf(
                FunctionCall("elseFun", listOf(Ident("i")))
            )))
        )
    }

    @Test
    fun `return statement is parsed`() {
        assertThat(parse("return 1 + 2")).isEqualTo(
            Return(BinOp(Literal(1), Operation.PLUS, Literal(2)))
        )
    }

    @Test
    fun `while statement is parsed`() {
        assertThat(parse("""
            while (i !: 2) {
                print(i)
            }
        """.trimIndent())).isEqualTo(
            While(BinOp(Ident("i"), Operation.NEQ, Literal(2)), Block(listOf(
                FunctionCall("print", listOf(Ident("i")))
            )))
        )
    }

    @Test
    fun `assignment statement is parsed`() {
        assertThat(parse("""
            i = 2
        """.trimIndent())).isEqualTo(
            Assignment("i", Literal(2))
        )
    }

    private fun parse(code: String): Statement {
        val lexer = FunLangLexer(CharStreams.fromString(code))
        val parser = FunLangParser(BufferedTokenStream(lexer))

        return StatementParser.visit(parser.parse())
    }

}