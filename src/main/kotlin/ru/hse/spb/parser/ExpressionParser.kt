package ru.hse.spb.parser

import org.antlr.v4.runtime.Token
import ru.hse.spb.funlang.*

object ExpressionParser : FunLangBaseVisitor<Expression>() {

    override fun visitVariableExpr(ctx: FunLangParser.VariableExprContext): Expression {
        return Ident(ctx.IDENT().text)
    }

    override fun visitLiteralExpr(ctx: FunLangParser.LiteralExprContext): Expression {
        return Literal(ctx.LITERAL().text.toInt())
    }

    override fun visitBinaryExpr(ctx: FunLangParser.BinaryExprContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)
        val op = parseOp(ctx.op)
        return BinOp(left, op, right)
    }

    override fun visitBracedExpr(ctx: FunLangParser.BracedExprContext): Expression {
        return visit(ctx.expr())
    }

    override fun visitFunctionCallExpr(ctx: FunLangParser.FunctionCallExprContext): Expression {
        val functionCall = ctx.functionCall()
        val functionName = functionCall.IDENT().text
        val arguments = functionCall.arguments().expr().map { visit(it) }

        return FunctionCall(functionName, arguments)
    }
}

private fun parseOp(op: Token): Operation = when (op.type) {
    FunLangParser.LT -> Operation.LT
    FunLangParser.GT -> Operation.GT
    FunLangParser.LTEQ -> Operation.LTEQ
    FunLangParser.GTEQ -> Operation.GTEQ
    FunLangParser.EQ -> Operation.EQ
    FunLangParser.NEQ -> Operation.NEQ
    FunLangParser.MULT -> Operation.MULTIPLY
    FunLangParser.DIV -> Operation.DIVIDE
    FunLangParser.MOD -> Operation.MOD
    FunLangParser.PLUS -> Operation.PLUS
    FunLangParser.MINUS -> Operation.MINUS

    else -> throw IllegalArgumentException("Illegal operator $op.")
}