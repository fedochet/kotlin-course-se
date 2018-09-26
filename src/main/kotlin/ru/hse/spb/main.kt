package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.interpreter.Context
import ru.hse.spb.interpreter.evalBlock
import ru.hse.spb.parser.BlockParser
import ru.hse.spb.parser.FunLangLexer
import ru.hse.spb.parser.FunLangParser

fun main(args: Array<String>) {
    val lexer = FunLangLexer(CharStreams.fromFileName(args[0]))
    val parser = FunLangParser(BufferedTokenStream(lexer))

    val block = BlockParser.visit(parser.parse())

    evalBlock(block, Context.empty())
}