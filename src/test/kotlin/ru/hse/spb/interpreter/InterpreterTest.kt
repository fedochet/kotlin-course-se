package ru.hse.spb.interpreter

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.hse.spb.funlang.*


class InterpreterTest {
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
    fun `builtin fun can be used`() {
        var savedArgs = emptyList<Int>()
        val ctx = Context.empty()
        ctx.addBuiltInFunction("print") { args ->
            savedArgs = args
            0
        }

        evalExpression(FunctionCall("print", listOf(Literal(1), Literal(2))), ctx)

        assertThat(savedArgs).containsExactly(1, 2)
    }

    @Test(expected = FunctionRedeclarationException::class)
    fun `two functions with same name cannot be in the same scope`() {
        val fun1 = FunctionDeclaration("fun1", emptyList(), Block(emptyList()))
        val blockWithDeclaration = Block(listOf(
            fun1,
            fun1
        ))

        evalBlock(blockWithDeclaration, Context.empty())
    }

    @Test
    fun `variable declared in block is added to context`() {
        val blockWithDeclaration = Block(listOf(
            VarDeclaration("x", Literal(1))
        ))

        val ctx = Context.empty()
        evalBlock(blockWithDeclaration, ctx)

        assertThat(ctx.getVariable("x")).isEqualTo(1)
    }

    @Test(expected = VariableRedeclarationException::class)
    fun `variable declared in block twice causes name conflict`() {
        val blockWithDeclaration = Block(listOf(
            VarDeclaration("x", Literal(1)),
            VarDeclaration("x", Literal(2))
        ))

        evalBlock(blockWithDeclaration, Context.empty())
    }

    @Test(expected = VariableRedeclarationException::class)
    fun `cannot declare variable with same name as function argument`() {
        val blockWithDeclaration = Block(listOf(VarDeclaration("x", Literal(1))))

        val function = FunctionDeclaration("fun1", listOf("x"), blockWithDeclaration)

        assertThat(call(function, listOf(Literal(1)), Context.empty()))
    }

    @Test(expected = VariableNotFound::class)
    fun `variable declared in then block cannot be used outside`() {
        val blockWithDeclaration = Block(listOf(
            If(Literal(1),
                Block(listOf(VarDeclaration("x", Literal(10))))),
            Ident("x")
        ))

        evalBlock(blockWithDeclaration, Context.empty())
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
        assertThat(evalExpression(Literal(1), Context.empty())).isEqualTo(1)
    }

    @Test
    fun `eval of ident is value of this variable in context`() {
        val context = Context.empty()
        context.declareVariable("x", 42)

        assertThat(evalExpression(Ident("x"), context)).isEqualTo(42)
    }

    @Test(expected = VariableNotFound::class)
    fun `eval of ident without variable throws exception`() {
        evalExpression(Ident("x"), Context.empty())
    }

    @Test
    fun `eval of binoperations works properly`() {
        assertThat(evalExpression(Literal(10) + Literal(5), Context.empty())).isEqualTo(15)
        assertThat(evalExpression(Literal(10) - Literal(5), Context.empty())).isEqualTo(5)
        assertThat(evalExpression(Literal(10) * Literal(5), Context.empty())).isEqualTo(50)
        assertThat(evalExpression(Literal(10) / Literal(5), Context.empty())).isEqualTo(2)
        assertThat(evalExpression(Literal(10) % Literal(5), Context.empty())).isEqualTo(0)
    }

    @Test
    fun `function with empty body returns zero`() {
        val fooFunction = FunctionDeclaration("foo", emptyList(), Block(emptyList()))

        assertThat(call(fooFunction, emptyList(), Context.empty())).isEqualTo(0)
    }

    @Test
    fun `identity function works`() {
        val identity = FunctionDeclaration("id", listOf("x"), Block(listOf(Return(Ident("x")))))

        assertThat(call(identity, listOf(Literal(1)), Context.empty())).isEqualTo(1)
        assertThat(call(identity, listOf(Literal(42)), Context.empty())).isEqualTo(42)
    }

    @Test
    fun `function call arguments can reference context`() {
        val identity = FunctionDeclaration("id", listOf("x"), Block(listOf(Return(Ident("x")))))

        val ctx = Context.empty()
        ctx.declareVariable("y", 42)

        assertThat(call(identity, listOf(Ident("y")), ctx)).isEqualTo(42)
    }

    @Test
    fun `function body can reference context`() {
        val closure = FunctionDeclaration(
            "foo",
            emptyList(),
            Block(listOf(Return(Ident("x") + Ident("y"))))
        )

        val ctx = Context.empty()
        ctx.declareVariable("x", 10)
        ctx.declareVariable("y", 20)

        assertThat(call(closure, emptyList(), ctx)).isEqualTo(30)
    }
}