package ch.chrigu.gmf.givemefeatures.shared.markdown

import com.github.difflib.text.DiffRowGenerator

object MarkdownDiff {
    private val generator = DiffRowGenerator.create()
        .showInlineDiffs(true)
        .mergeOriginalRevised(true)
        .inlineDiffByWord(true)
        .oldTag { f: Boolean? -> "~" } //introduce markdown style for strikethrough
        .newTag { f: Boolean? -> "**" } //introduce markdown style for bold
        .build()

    fun diff(original: Markdown, revised: Markdown?): Markdown {
        val rows = generator.generateDiffRows(original.toString().lines(), revised.toString().lines())
        return Markdown(rows.joinToString("\n") { it.oldLine })
    }
}
