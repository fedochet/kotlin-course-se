import org.junit.Assert.assertEquals
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
            emptyDocument.render()
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
            basicDocument.render()
        )
    }

    @Test
    fun `document can contain text`() {
        val documentWithText = document {
            documentClass("article")
            +"text inside document"
            +"text on another line"
        }

        assertEquals(
            """
            \documentclass{article}
            \begin{document}
            text inside document
            text on another line
            \end{document}
            """.trimIndent(),
            documentWithText.render()
        )
    }

    @Test
    fun `document can contain itemized list`() {
        val documentWithText = document {
            itemize {
                item { +"item 1" }
                item { +"item 2" }
                itemize {
                    item { +"nested item" }
                }
            }
        }

        assertEquals(
            """
            \begin{document}
            \begin{itemize}
            \item item 1
            \item item 2
            \begin{itemize}
            \item nested item
            \end{itemize}
            \end{itemize}
            \end{document}
            """.trimIndent(),
            documentWithText.render()
        )
    }

    @Test
    fun `document can contain enumerated list`() {
        val documentWithText = document {
            enumerate {
                item { +"item 1" }
                item { +"item 2" }
                enumerate {
                    item { +"nested item" }
                }
            }
        }

        assertEquals(
            """
            \begin{document}
            \begin{enumerate}
            \item item 1
            \item item 2
            \begin{enumerate}
            \item nested item
            \end{enumerate}
            \end{enumerate}
            \end{document}
            """.trimIndent(),
            documentWithText.render()
        )
    }


    @Test
    fun `document can contain math equations`() {
        document {

        }
    }
}