package ru.hse.spb.interpreter

import ru.hse.spb.funlang.*
import ru.hse.spb.funlang.Operation.*
import java.lang.RuntimeException

class VariableNotFound(val name: String) : RuntimeException()

class Context private constructor(val parent: Context? = null) {
    private val variables: MutableMap<String, Int> = mutableMapOf();

    fun declareFunction(declaration: FunctionDeclaration) {}
    fun findFunction(): FunctionDeclaration? = null

    fun declareVariable(name: String, initializer: Int) {
        variables[name] = initializer
    }

    fun getVariable(name: String): Int? = variables[name]
    fun setVariable(name: String) {}

    fun derive(): Context = Context(this)

    companion object {
        fun empty() = Context()
    }
}

sealed class BlockExecutionResult
data class ReturnResult(val value: Int) : BlockExecutionResult()
object NoReturnResult : BlockExecutionResult()

fun evalBlock(block: Block, ctx: Context): Int {
    val result = executeBlock(block, ctx)

    return if (result is ReturnResult) result.value else 0
}

fun evalStatement(statement: Expression, ctx: Context): Int {
    return when (statement) {
        is Literal -> statement.value
        is Ident -> ctx.getVariable(statement.name) ?: throw VariableNotFound(statement.name)
        is BinOp -> evalBinop(evalStatement(statement.left, ctx), statement.op, (evalStatement(statement.right, ctx)))
        else -> TODO()
    }
}

private fun executeBlock(block: Block, ctx: Context): BlockExecutionResult {
    for (statement in block.statements) {
        when (statement) {
            is Return -> return ReturnResult(evalStatement(statement.value, ctx))
            is If -> {
                val condition = evalStatement(statement.condition, ctx)
                val result = if (condition != 0) {
                    executeBlock(statement.thenBlock, ctx)
                } else {
                    statement.elseBlock?.let { executeBlock(it, ctx) }
                }

                if (result is ReturnResult) return result
            }
        }
    }

    return NoReturnResult
}

private fun evalBinop(left: Int, op: Operation, right: Int): Int =
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

private fun Int.toBool() = this != 0
private fun Boolean.toInt() = if (this) 1 else 0