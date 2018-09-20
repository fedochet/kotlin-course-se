package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpressionParser
import ru.hse.spb.parser.FunLangLexer
import ru.hse.spb.parser.FunLangParser

fun main(args: Array<String>) {

    val funLangLexer = FunLangLexer(CharStreams.fromString("1+2"))
    val parse = ExpressionParser.visit(FunLangParser(BufferedTokenStream(funLangLexer)).parse())

    println(parse)
}