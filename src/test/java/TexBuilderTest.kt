import org.junit.Assert.*
import org.junit.Test

class TexBuilderTest {
    @Test
    fun `empty document is rendered`() {
        val emptyDocument = document {}

        assertEquals(
            """
            \begin{document}
            \end{document}
            """.trimIndent(),
            emptyDocument.renderToString()
        )
    }

    @Test
    fun `basic document properties are correctly rendered`() {
        val basicDocument = document {
            documentClass("beamer")
            usepackage("package1", "par1", "par2")
            usepackage("package2")
        }

        assertEquals(
            """
            \documentclass{beamer}
            \usepackage[par1,par2]{package1}
            \usepackage{package2}
            \begin{document}
            \end{document}
            """.trimIndent(),
            basicDocument.renderToString()
        )
    }

    @Test
    fun `document can contain text`() {
        val documentWithText = document {
            documentClass("article")
            + "text inside document"
            + "text on another line"
        }

        assertEquals(
            """
            \documentclass{article}
            \begin{document}
            text inside document
            text on another line
            \end{document}
            """.trimIndent(),
            documentWithText.renderToString()
        )
    }
}