package ru.hse.spb.funlang

// All classes are data classes to ease up debugging.

data class Block(val statements: List<Statement>)
val EMPTY_BLOCK = Block(emptyList())

sealed class Statement
data class VarDeclaration(val name: String, val initializer: Expression?): Statement()
data class FunctionDeclaration(val name: String, val args: List<String>, val body: Block): Statement()
data class While(val condition: Expression, val body: Block) : Statement()
data class If(val condition: Expression, val thenBlock: Block, val elseBlock: Block = EMPTY_BLOCK) : Statement()
data class Assignment(val name: String, val value: Expression) : Statement()
data class Return(val value: Expression) : Statement()

sealed class Expression : Statement()
data class FunctionCall(val name: String, val args: List<Expression>) : Expression()
data class BinOp(val left: Expression, val op: Operation, val right: Expression) : Expression()
data class Ident(val name: String) : Expression()
data class Literal(val value: Int) : Expression()

enum class Operation {
    MULTIPLY, DIVIDE, MOD,
    PLUS, MINUS,
    OR, AND,
    GT, LT, GTEQ, LTEQ,
    EQ, NEQ,
}

operator fun Expression.plus(other: Expression) = BinOp(this, Operation.PLUS, other)
operator fun Expression.minus(other: Expression) = BinOp(this, Operation.MINUS, other)
operator fun Expression.times(other: Expression) = BinOp(this, Operation.MULTIPLY, other)
operator fun Expression.div(other: Expression) = BinOp(this, Operation.DIVIDE, other)
operator fun Expression.rem(other: Expression) = BinOp(this, Operation.MOD, other)