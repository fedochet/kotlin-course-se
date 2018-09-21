package ru.hse.spb.parser

import ru.hse.spb.funlang.Block

object BlockParser : FunLangBaseVisitor<Block>() {
    override fun visitBlock(ctx: FunLangParser.BlockContext): Block {
        val statements = ctx.statement().map { StatementParser.visit(it) }
        return Block(statements)
    }

    override fun visitBlockWithBraces(ctx: FunLangParser.BlockWithBracesContext): Block {
        return visit(ctx.block())
    }
}