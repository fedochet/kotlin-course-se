package ru.hse.spb.parser

import ru.hse.spb.funlang.*

object StatementParser : FunLangBaseVisitor<Statement>() {

    override fun visitFunction(ctx: FunLangParser.FunctionContext): Statement {
        val functionName = ctx.IDENT().text
        val arguments = ctx.parameterNames().IDENT().map { it.text }
        val body: Block = BlockParser.visit(ctx.blockWithBraces())

        return FunctionDeclaration(functionName, arguments, body)
    }

    override fun visitVariable(ctx: FunLangParser.VariableContext): Statement {
        val name = ctx.IDENT().text
        val initializer = ctx.expr()?.let { ExpressionParser.visit(it) }

        return VarDeclaration(name, initializer)
    }

    override fun visitWhileStmt(ctx: FunLangParser.WhileStmtContext): Statement {
        val cond = ExpressionParser.visit(ctx.expr())
        val bodyBlock = BlockParser.visit(ctx.blockWithBraces())

        return While(cond, bodyBlock)
    }

    override fun visitIfStmt(ctx: FunLangParser.IfStmtContext): Statement {
        val cond = ExpressionParser.visit(ctx.expr())
        val thenBlock = BlockParser.visit(ctx.thenBlock)
        val elseBlock = ctx.elseBlock?.let { BlockParser.visit(it) }

        return If(cond, thenBlock, elseBlock)
    }

    override fun visitAssignmentStmt(ctx: FunLangParser.AssignmentStmtContext): Statement {
        val name = ctx.IDENT().text
        val value = ExpressionParser.visit(ctx.expr())

        return Assignment(name, value)
    }

    override fun visitReturnStmt(ctx: FunLangParser.ReturnStmtContext): Statement {
        return Return(ExpressionParser.visit(ctx.expr()))
    }

    override fun visitExprStmt(ctx: FunLangParser.ExprStmtContext): Statement {
        return ExpressionParser.visit(ctx.expr())
    }

}