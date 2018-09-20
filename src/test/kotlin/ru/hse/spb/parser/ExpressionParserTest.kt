package ru.hse.spb.parser

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.assertj.core.api.Assertions.*
import org.junit.Test
import ru.hse.spb.funlang.*

class ExpressionParserTest {

    @Test
    fun `number literals are parsed`() {
        assertThat(parse("1")).isEqualTo(Literal(1))
        assertThat(parse("0")).isEqualTo(Literal(0))
        assertThat(parse("123")).isEqualTo(Literal(123))
    }

    @Test
    fun `identifcators are parsed`() {
        assertThat(listOf(parse("a"), parse("b"), parse("abcd")))
            .containsExactly(Ident("a"), Ident("b"), Ident("abcd"))
    }

    @Test
    fun `simple binary expressions are parsed`() {
        assertThat(parse("1+2")).isEqualTo(Literal(1) + Literal(2))
        assertThat(parse("1-2")).isEqualTo(Literal(1) - Literal(2))
        assertThat(parse("1*2")).isEqualTo(Literal(1) * Literal(2))
        assertThat(parse("1/2")).isEqualTo(Literal(1) / Literal(2))
        assertThat(parse("1%2")).isEqualTo(Literal(1) % Literal(2))
    }

    @Test
    fun `multiplication operation have higher priority than additional`() {
        assertThat(parse("1+2*3")).isEqualTo(
            Literal(1) + (Literal(2) * Literal(3))
        )

        assertThat(parse("1/2-3")).isEqualTo(
            (Literal(1) / Literal(2)) - Literal(3)
        )
    }

    @Test
    fun `expression in braces is parsed`() {
        assertThat(parse("((((((1)))+((((2)))))))"))
            .isEqualTo(Literal(1) + Literal(2))
    }

    @Test
    fun `parenthesis change priority`() {
        assertThat(parse("(1+2)*3")).isEqualTo(
            (Literal(1) + Literal(2)) * Literal(3)
        )

        assertThat(parse("1/(2-3)")).isEqualTo(
            Literal(1) / (Literal(2) - Literal(3))
        )
    }

    @Test
    fun `simple comparisons are parsed`() {
        assertThat(parse("1<2")).isEqualTo(BinOp(Literal(1), Operation.LT, Literal(2)))
        assertThat(parse("1>2")).isEqualTo(BinOp(Literal(1), Operation.GT, Literal(2)))
        assertThat(parse("1<:2")).isEqualTo(BinOp(Literal(1), Operation.LTEQ, Literal(2)))
        assertThat(parse("1>:2")).isEqualTo(BinOp(Literal(1), Operation.GTEQ, Literal(2)))
        assertThat(parse("1::2")).isEqualTo(BinOp(Literal(1), Operation.EQ, Literal(2)))
        assertThat(parse("1!:2")).isEqualTo(BinOp(Literal(1), Operation.NEQ, Literal(2)))
    }



    @Test
    fun `comparisons have higher priority than arithmetics`() {
        assertThat(parse("1+2<3+4")).isEqualTo(
            BinOp(Literal(1) + Literal(2), Operation.LT, Literal(3) + Literal(4))
        )

        assertThat(parse("1*2>3/4")).isEqualTo(
            BinOp(Literal(1) * Literal(2), Operation.GT, Literal(3) / Literal(4))
        )
    }

    private fun parse(code: String): Expression {
        val lexer = FunLangLexer(CharStreams.fromString(code))
        val parser = FunLangParser(BufferedTokenStream(lexer))

        return ExpressionParser.visit(parser.parse())
    }

    @Test
    fun `function calls are parsed`() {
        assertThat(parse("func1(func2(),func3(3),func4(4,5))"))
            .isEqualTo(FunctionCall("func1", listOf(
                FunctionCall("func2", emptyList()),
                FunctionCall("func3", listOf(Literal(3))),
                FunctionCall("func4", listOf(Literal(4), Literal(5)))
            )))
    }

    @Test
    fun `func calls work with equations`() {
        assertThat(parse("func1(1)+func2(2)/func3(3+4)<:func4(5)"))
            .isEqualTo(BinOp(
                FunctionCall("func1", listOf(Literal(1))) +
                    (FunctionCall("func2", listOf(Literal(2))) /
                        FunctionCall("func3", listOf((Literal(3) + Literal(4))))),
                Operation.LTEQ,
                FunctionCall("func4", listOf(Literal(5)))
            ))
    }

    // convinience operators to ease up testing
    private operator fun Expression.plus(other: Expression) = BinOp(this, Operation.PLUS, other)
    private operator fun Expression.minus(other: Expression) = BinOp(this, Operation.MINUS, other)
    private operator fun Expression.times(other: Expression) = BinOp(this, Operation.MULTIPLY, other)
    private operator fun Expression.div(other: Expression) = BinOp(this, Operation.DIVIDE, other)
    private operator fun Expression.rem(other: Expression) = BinOp(this, Operation.MOD, other)
}