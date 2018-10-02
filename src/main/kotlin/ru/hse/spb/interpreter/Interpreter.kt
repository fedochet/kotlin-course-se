package ru.hse.spb.interpreter

import ru.hse.spb.funlang.*
import ru.hse.spb.funlang.Operation.*

class VariableNotFound(val name: String) : RuntimeException()
class VariableRedeclarationException(val name: String) : RuntimeException()
class WrongNumberOfArgsException(val expected: List<String>, val actual: List<Expression>) : RuntimeException()
class FunctionRedeclarationException(val name: String) : RuntimeException()

class ContextFunction(val callable: (List<Expression>, Context) -> Int) {
    fun call(args: List<Expression>, ctx: Context): Int {
        return callable(args, ctx)
    }
}

class Context private constructor(private val parent: Context? = null) {
    private val variables: MutableMap<String, kotlin.Int> = mutableMapOf()
    private val functions: MutableMap<String, ContextFunction> = mutableMapOf()

    fun addBuiltInFunction(name: String, function: (List<Int>) -> Int) {
        if (name in functions) throw FunctionRedeclarationException(name)
        functions[name] = ContextFunction { args, ctx ->
            function(args.map { evalExpression(it, ctx) })
        }
    }

    fun declareFunction(declaration: FunctionDeclaration) {
        if (declaration.name in functions) throw FunctionRedeclarationException(declaration.name)
        functions[declaration.name] = ContextFunction { args, ctx -> call(declaration, args, ctx) }
    }

    fun findFunction(name: String): ContextFunction {
        return functions[name]
            ?: parent?.findFunction(name)
            ?: throw VariableNotFound(name)
    }

    fun declareVariable(name: String, initializer: kotlin.Int) {
        if (name in variables) throw VariableRedeclarationException(name)
        variables[name] = initializer
    }

    fun getVariable(name: String): kotlin.Int {
        return variables[name]
            ?: parent?.getVariable(name)
            ?: throw VariableNotFound(name)
    }

    fun setVariable(name: String, value: kotlin.Int) {
        when {
            name in variables -> variables[name] = value
            parent != null -> parent.setVariable(name, value)
            else -> throw VariableNotFound(name)
        }
    }

    fun derive(): Context = Context(this)

    companion object {
        fun empty() = Context()
    }
}

fun evalBlock(block: Block, ctx: Context): Int = executeBlock(block, ctx) ?: 0

fun call(function: FunctionDeclaration, args: List<Expression>, ctx: Context): kotlin.Int {
    if (function.args.size != args.size) throw WrongNumberOfArgsException(function.args, args)

    val functionCtx = ctx.derive()
    for ((name, value) in function.args.zip(args)) {
        functionCtx.declareVariable(name, evalExpression(value, ctx))
    }

    return evalBlock(function.body, functionCtx)
}

private fun executeBlock(block: Block, ctx: Context): Int? {
    for (statement in block.statements) {
        executeStatement(statement, ctx)?.let { return it }
    }

    return null
}

private fun executeStatement(statement: Statement, ctx: Context): Int? {
    when (statement) {
        is Return -> return evalExpression(statement.value, ctx)

        is If -> {
            val condition = evalExpression(statement.condition, ctx)
            return executeBlock(
                if (condition != 0) statement.thenBlock else statement.elseBlock,
                ctx.derive()
            )
        }

        is FunctionDeclaration -> ctx.declareFunction(statement)

        is VarDeclaration -> {
            val initializer = statement.initializer?.let { evalExpression(it, ctx) } ?: 0
            ctx.declareVariable(statement.name, initializer)
        }

        is Expression -> evalExpression(statement, ctx)

        is Assignment -> ctx.setVariable(statement.name, evalExpression(statement.value, ctx))

        is While -> {
            while (evalExpression(statement.condition, ctx).toBool()) {
                executeBlock(statement.body, ctx.derive())?.let { return it }
            }
        }
    }

    return null
}

fun evalExpression(expr: Expression, ctx: Context): kotlin.Int {
    return when (expr) {
        is Literal -> expr.value
        is Ident -> ctx.getVariable(expr.name)
        is BinOp -> evalBinop(evalExpression(expr.left, ctx), expr.op, (evalExpression(expr.right, ctx)))
        is FunctionCall -> ctx.findFunction(expr.name).call(expr.args, ctx)
    }
}

private fun evalBinop(left: kotlin.Int, op: Operation, right: kotlin.Int): kotlin.Int =
    when (op) {
        MULTIPLY -> left * right
        DIVIDE -> left / right
        MOD -> left % right
        PLUS -> left + right
        MINUS -> left - right
        OR -> (left.toBool() || right.toBool()).toInt()
        AND -> (left.toBool() && right.toBool()).toInt()
        GT -> (left > right).toInt()
        LT -> (left < right).toInt()
        GTEQ -> (left >= right).toInt()
        LTEQ -> (left <= right).toInt()
        EQ -> (left == right).toInt()
        NEQ -> (left != right).toInt()
    }

private fun kotlin.Int.toBool() = this != 0
private fun Boolean.toInt() = if (this) 1 else 0