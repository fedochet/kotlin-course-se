package ru.hse.spb.parser

import ru.hse.spb.funlang.*

object ExpressionParser : FunLangBaseVisitor<Expression>() {

    override fun visitVariableExpr(ctx: FunLangParser.VariableExprContext): Expression {
        return Ident(ctx.IDENT().text)
    }

    override fun visitLiteralExpr(ctx: FunLangParser.LiteralExprContext): Expression {
        return Literal(ctx.LITERAL().text.toInt())
    }

    override fun visitAdditiveExpr(ctx: FunLangParser.AdditiveExprContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)

        val op = when (ctx.op.type) {
            FunLangParser.PLUS -> Operation.PLUS
            FunLangParser.MINUS ->  Operation.MINUS
            else -> throw IllegalArgumentException("Illegal operation in $ctx (should be `plus` or `minus`).")
        }

        return BinOp(left, op, right)
    }

    override fun visitMultiplicationExpr(ctx: FunLangParser.MultiplicationExprContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)

        val op = when (ctx.op.type) {
            FunLangParser.MULT -> Operation.MULTIPLY
            FunLangParser.DIV ->  Operation.DIVIDE
            FunLangParser.MOD -> Operation.MOD
            else -> throw IllegalArgumentException("Illegal operation in $ctx (should be `*`, `%` or `/`).")
        }

        return BinOp(left, op, right)
    }

    override fun visitRelationalExpr(ctx: FunLangParser.RelationalExprContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)

        val op = when (ctx.op.type) {
            FunLangParser.LT -> Operation.LT
            FunLangParser.GT -> Operation.GT
            FunLangParser.LTEQ -> Operation.LTEQ
            FunLangParser.GTEQ -> Operation.GTEQ
            FunLangParser.EQ -> Operation.EQ
            FunLangParser.NEQ -> Operation.NEQ
            else -> throw IllegalArgumentException("Illegal operation in $ctx.")
        }

        return BinOp(left, op, right)
    }

    override fun visitBracedExpr(ctx: FunLangParser.BracedExprContext): Expression {
        return visit(ctx.expr())
    }

    override fun visitFunctionCallExpr(ctx: FunLangParser.FunctionCallExprContext): Expression {
        val functionCall = ctx.functionCall()
        val functionName = functionCall.IDENT().text
        val arguments = functionCall.arguments().expr().mapNotNull { visit(it) }

        return FunctionCall(functionName, arguments)
    }
}