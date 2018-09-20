package ru.hse.spb.funlang

data class Block(val statemens: List<Statement>)

sealed class Statement
class VarDeclaration(val name: String, val initializer: Expression?): Statement()
class FunctionDeclaration(val name: String, val args: List<String>): Statement()
class While(val condition: Expression, val body: Block) : Statement()
class If(val condition: Expression, val thenBlock: Block, val elseBlock: Block?) : Statement()
class Assignment(val name: String, val value: Expression) : Statement()

sealed class Expression
class FunctionCall(val name: String, val args: List<Expression>) : Expression()
class BioOp(val left: Expression, val op: Operation, val right: Expression) : Expression()
class Ident(val name: String) : Expression()
class Literal(val value: Int) : Expression()

enum class Operation {
    MULTIPLY, DIVIDE, MOD,
    PLUS, MINUS,
    OR, AND,
    GT, LT, GTEQ, LTEQ,
    EQ, NEQ,
}