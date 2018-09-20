package ru.hse.spb.parser

import ru.hse.spb.funlang.*

object ExpressionParser : FunLangBaseVisitor<Expression>() {

    override fun visitLiteralExpr(ctx: FunLangParser.LiteralExprContext): Expression {
        return Literal(ctx.LITERAL().text.toInt())
    }

    override fun visitAdditiveExpr(ctx: FunLangParser.AdditiveExprContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)

        return when (ctx.op.type) {
            FunLangParser.PLUS -> BinOp(left, Operation.PLUS, right)
            FunLangParser.MINUS -> BinOp(left, Operation.MINUS, right)
            else -> throw IllegalArgumentException("Illegal operation in $ctx (should be `plus` or `minus`).")
        }
    }

    override fun visitFunctionCallExpr(ctx: FunLangParser.FunctionCallExprContext): Expression {
        val functionCall = ctx.functionCall()
        val functionName = functionCall.IDENT().text
//        val arguments = functionCall.arguments().children.map { visit(it) }
//
        return FunctionCall(functionName, emptyList())
    }
}