import java.io.ByteArrayOutputStream
import java.io.OutputStream

val LINE_SEPARATOR = System.getProperty("line.separator");

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

abstract class TexTag : Renderable {
    abstract val name: String
    protected val content: MutableList<String> = mutableListOf()

    fun itemize(action: TexList.() -> Unit) {
        content.add(TexList.itemized().apply(action).render())
    }

    fun enumerate(action: TexList.() -> Unit) {
        content.add(TexList.enumerated().apply(action).render())
    }

    override fun render(): String {
        return buildString {
            appendln("\\begin{$name}")
            for (item in content) {
                appendln(item)
            }
            append("\\end{$name}")
        }
    }
}

@EnumerationMarker
class TexList private constructor(override val name: String) : TexTag() {
    fun item(builder: Item.() -> Unit) {
        content.add(Item().apply(builder).render())
    }

    companion object {
        fun enumerated() = TexList("enumerate")
        fun itemized() = TexList("itemize")
    }
}

class BeamerFrame : TexTag() {
    override val name: String = "frame"
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

    fun equation(builder: EquationEnv.() -> Unit) {
        content.add(EquationEnv().apply(builder).render())
    }

    fun customTag(name: String, vararg params: Param, builder: GenericTag.() -> Unit) = GenericTag().apply(builder)

    private fun TexPackage.render(): String {
        val formattedOptions = if (options.isNotEmpty()) options.joinToString(",", "[", "]") else ""
        return "\\usepackage$formattedOptions{$name}"
    }
}

class EquationEnv : Renderable {
    private val content: MutableList<String> = mutableListOf()

    override fun render(): String {
        return content.joinToString(LINE_SEPARATOR, "\\begin{equation}$LINE_SEPARATOR", "$LINE_SEPARATOR\\end{equation}")
    }

    operator fun String.unaryPlus() {
        content.add(this)
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