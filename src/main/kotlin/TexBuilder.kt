import java.io.ByteArrayOutputStream
import java.io.OutputStream

typealias Param = Pair<String, String>

data class TexPackage(val name: String, val options: List<String>)

@DslMarker
annotation class EnumerationMarker

@EnumerationMarker
class Item {
    operator fun String.unaryPlus() {
        TODO()
    }
}

@EnumerationMarker
class Enumeration {
    fun item(builder: Item.() -> Unit) = Item().apply(builder)
}

class BeamerFrame {
    fun itemize(action: Enumeration.() -> Unit) = Enumeration().apply(action)
}

class GenericTag {
    operator fun String.unaryPlus() {
        TODO()
    }
}

class TexBuilder {
    private var documentClass: String? = null
    private val packages: MutableList<TexPackage> = mutableListOf()
    private val content: MutableList<String> = mutableListOf()

    fun documentClass(name: String) {
        documentClass = name
    }

    fun usepackage(name: String, vararg options: String) {
        packages.add(TexPackage(name, options.asList()))
    }

    operator fun String.unaryPlus() {
        content.add(this)
    }

    // todo forbid to use frames inside frames
    fun frame(frameTitle: String, vararg params: Param, builder: BeamerFrame.() -> Unit) {
        TODO()
    }

    fun toOutputStream(stream: OutputStream) {
        stream.writer().run {
            documentClass?.let {
                appendln("\\documentclass{$it}")
            }

            for (pkg in packages) {
                appendln(pkg.render())
            }

            appendln("\\begin{document}")

            for (entry in content) {
                appendln(entry)
            }

            append("\\end{document}")
        }.flush()
    }

    fun customTag(name: String, vararg params: Param, builder: GenericTag.() -> Unit) = GenericTag().apply(builder)

    private fun TexPackage.render(): String {
        val formattedOptions = if (options.isNotEmpty()) options.joinToString(",", "[", "]") else ""
        return "\\usepackage$formattedOptions{$name}"
    }
}

fun TexBuilder.renderToString() = ByteArrayOutputStream().also { toOutputStream(it) }.toString()

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