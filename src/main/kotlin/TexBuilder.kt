import java.io.ByteArrayOutputStream
import java.io.OutputStream

typealias Param = Pair<String, String>

data class TexPackage(val name: String, val options: List<String>)

interface Renderable {
    fun render(): String
}

@DslMarker
annotation class EnumerationMarker

@EnumerationMarker
class Item : Renderable {
    private val buffer = StringBuilder()
    operator fun String.unaryPlus() {
        buffer.append(this)
    }

    override fun render() = "\\item $buffer"
}

@EnumerationMarker
class TexList private constructor(private val name: String) : Renderable {
    private val items: MutableList<Renderable> = mutableListOf()

    fun item(builder: Item.() -> Unit) {
        items.add(Item().apply(builder))
    }

    fun itemize(action: TexList.() -> Unit) {
        items.add(itemized().apply(action))
    }

    fun enumerate(action: TexList.() -> Unit) {
        items.add(enumerated().apply(action))
    }

    override fun render(): String {
        val buffer = StringBuilder()

        buffer.appendln("\\begin{$name}")
        for (item in items) {
            buffer.appendln(item.render())
        }
        buffer.append("\\end{$name}")

        return buffer.toString()
    }

    companion object {
        fun enumerated() = TexList("enumerate")
        fun itemized() = TexList("itemize")
    }
}

class BeamerFrame {
    fun itemize(action: TexList.() -> Unit) = TexList.itemized().apply(action)
    fun enumerate(action: TexList.() -> Unit) = TexList.enumerated().apply(action)
}

class GenericTag {
    operator fun String.unaryPlus() {
        TODO()
    }
}

class TexBuilder : Renderable {
    private var documentClass: String? = null

    private val packages: MutableList<TexPackage> = mutableListOf()
    private val content: MutableList<String> = mutableListOf()

    override fun render(): String = ByteArrayOutputStream().also { toOutputStream(it) }.toString()

    fun documentClass(name: String) {
        documentClass = name
    }

    fun usepackage(name: String, vararg options: String) {
        packages.add(TexPackage(name, options.asList()))
    }

    operator fun String.unaryPlus() {
        content.add(this)
    }

    fun itemize(action: TexList.() -> Unit) {
        content.add(TexList.itemized().apply(action).render())
    }

    fun enumerate(action: TexList.() -> Unit) {
        content.add(TexList.enumerated().apply(action).render())
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