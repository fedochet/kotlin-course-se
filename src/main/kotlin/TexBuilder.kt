import java.io.OutputStream

typealias Param = Pair<String, String>

class Item {
    operator fun String.unaryPlus() {
        TODO()
    }
}

class Itemizer {
    fun item(builder: Item.() -> Unit) = Item().apply(builder)
}

class BeamerFrame {
    fun itemize(action: Itemizer.() -> Unit) = Itemizer().apply(action)
}

class GenericTag {
    operator fun String.unaryPlus() {
        TODO()
    }
}

class TexBuilder {
    fun toOutputStream(stream: OutputStream) {
        TODO()
    }

    fun documentClass(name: String) {
        TODO()
    }

    fun usepackage(vararg packages: String) {
        TODO()
    }

    fun frame(frameTitle: String, vararg params: Param, builder: BeamerFrame.() -> Unit) {
        TODO()
    }

    fun customTag(name: String, vararg params: Param, builder: GenericTag.() -> Unit) = GenericTag().apply(builder)
}

fun document(builder: TexBuilder.() -> Unit) = TexBuilder().apply(builder)

fun main(args: Array<String>) {
    val rows = listOf("item 1", "item 2")

    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)
        frame("frametitle", "arg1" to "arg2") {
            itemize {
                for (row in rows) {
                    item { +"$row text" }
                }
            }

            // begin{pyglist}[language=kotlin]...\end{pyglist}
            customTag("pyglist", "language" to "kotlin") {
                +"""
               |val a = 1
               |
            """
            }
        }
    }.toOutputStream(System.out)
}