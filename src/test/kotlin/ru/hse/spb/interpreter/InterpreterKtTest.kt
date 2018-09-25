package ru.hse.spb.interpreter

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.hse.spb.funlang.*


internal class InterpreterKtTest {
    @Test
    fun `eval of empty block returns zero`() {
        assertThat(evalBlock(Block(emptyList()), Context.empty()))
            .isEqualTo(0)
    }

    @Test
    fun `eval of block with single return is value of that return`() {
        val blockWithReturn = Block(listOf(Return(Literal(2))))

        assertThat(evalBlock(blockWithReturn, Context.empty()))
            .isEqualTo(2)
    }

    @Test
    fun `eval of block with if depends on value`() {
        val blockWithIfThen = Block(listOf(
            If(condition = Literal(1),
                thenBlock = Block(listOf(Return(Literal(2)))),
                elseBlock = Block(listOf(Return(Literal(3)))))
        ))

        val blockWithIfElse = Block(listOf(
            If(condition = Literal(0),
                thenBlock = Block(listOf(Return(Literal(2)))),
                elseBlock = Block(listOf(Return(Literal(3)))))
        ))

        assertThat(evalBlock(blockWithIfThen, Context.empty())).isEqualTo(2)
        assertThat(evalBlock(blockWithIfElse, Context.empty())).isEqualTo(3)
    }

    @Test
    fun `eval of literal is its value`() {
        assertThat(evalStatement(Literal(1), Context.empty())).isEqualTo(1)
    }

    @Test
    fun `eval of ident is value of this variable in context`() {
        val context = Context.empty()
        context.declareVariable("x", 42)

        assertThat(evalStatement(Ident("x"), context)).isEqualTo(42)
    }

    @Test(expected = VariableNotFound::class)
    fun `eval of ident without variable throws exception`() {
        evalStatement(Ident("x"), Context.empty())
    }

    @Test
    fun `eval of binoperations works properly`() {
        assertThat(evalStatement(Literal(10) + Literal(5), Context.empty())).isEqualTo(15)
        assertThat(evalStatement(Literal(10) - Literal(5), Context.empty())).isEqualTo(5)
        assertThat(evalStatement(Literal(10) * Literal(5), Context.empty())).isEqualTo(50)
        assertThat(evalStatement(Literal(10) / Literal(5), Context.empty())).isEqualTo(2)
        assertThat(evalStatement(Literal(10) % Literal(5), Context.empty())).isEqualTo(0)
    }
}