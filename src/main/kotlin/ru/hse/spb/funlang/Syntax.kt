package ru.hse.spb.funlang

// All classes are data classes to ease up debugging.

data class Block(val statemens: List<Statement>)

sealed class Statement
data class VarDeclaration(val name: String, val initializer: Expression?): Statement()
data class FunctionDeclaration(val name: String, val args: List<String>, val body: Block): Statement()
data class While(val condition: Expression, val body: Block) : Statement()
data class If(val condition: Expression, val thenBlock: Block, val elseBlock: Block?) : Statement()
data class Assignment(val name: String, val value: Expression) : Statement()

sealed class Expression
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