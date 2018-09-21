package ru.hse.spb.parser

import ru.hse.spb.funlang.Block
import ru.hse.spb.funlang.FunctionDeclaration
import ru.hse.spb.funlang.If
import ru.hse.spb.funlang.Statement

object StatementParser : FunLangBaseVisitor<Statement>() {
    override fun visitFunction(ctx: FunLangParser.FunctionContext): Statement {
        val functionName = ctx.IDENT().text
        val arguments = ctx.parameterNames().IDENT().map { it.text }
        val body: Block = BlockParser.visit(ctx.blockWithBraces())

        return FunctionDeclaration(functionName, arguments, body)
    }

    override fun visitIfStmt(ctx: FunLangParser.IfStmtContext): Statement {
        val cond = ExpressionParser.visit(ctx.expr())
        val thenBlock = BlockParser.visit(ctx.thenBlock)
        val elseBlock = BlockParser.visit(ctx.elseBlock)

        return If(cond, thenBlock, elseBlock)
    }
}