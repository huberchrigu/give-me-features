package ch.chrigu.gmf.givemefeatures.shared.markdown

import com.github.difflib.DiffUtils
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

    fun merge3(base: Markdown, ours: Markdown, theirs: Markdown?): Markdown {
        val baseLines = base.toString().lines()
        val ourLines = ours.toString().lines()
        val theirLines = theirs?.toString()?.lines() ?: emptyList()

        val patchOurs = DiffUtils.diff(baseLines, ourLines)
        val patchTheirs = DiffUtils.diff(baseLines, theirLines)

        val result = mutableListOf<String>()
        var baseIndex = 0

        val changesOurs = patchOurs.deltas.iterator()
        val changesTheirs = patchTheirs.deltas.iterator()

        var currentOurs = if (changesOurs.hasNext()) changesOurs.next() else null
        var currentTheirs = if (changesTheirs.hasNext()) changesTheirs.next() else null

        while (baseIndex < baseLines.size) {
            val oursPos = currentOurs?.source?.position ?: Int.MAX_VALUE
            val theirsPos = currentTheirs?.source?.position ?: Int.MAX_VALUE

            when {
                baseIndex < minOf(oursPos, theirsPos) -> {
                    result.add(baseLines[baseIndex])
                    baseIndex++
                }

                oursPos == theirsPos -> {
                    val oursChange = currentOurs!!
                    val theirsChange = currentTheirs!!

                    if (oursChange.target.lines == theirsChange.target.lines) {
                        result.addAll(oursChange.target.lines)
                    } else {
                        // Conflict
                        result.add("<<<<<<< OURS")
                        result.addAll(oursChange.target.lines)
                        result.add("=======")
                        result.addAll(theirsChange.target.lines)
                        result.add(">>>>>>> THEIRS")
                    }

                    baseIndex = oursChange.source.position + oursChange.source.lines.size
                    currentOurs = if (changesOurs.hasNext()) changesOurs.next() else null
                    currentTheirs = if (changesTheirs.hasNext()) changesTheirs.next() else null
                }

                oursPos < theirsPos -> {
                    val change = currentOurs!!
                    result.addAll(change.target.lines)
                    baseIndex = change.source.position + change.source.lines.size
                    currentOurs = if (changesOurs.hasNext()) changesOurs.next() else null
                }

                else -> {
                    val change = currentTheirs!!
                    result.addAll(change.target.lines)
                    baseIndex = change.source.position + change.source.lines.size
                    currentTheirs = if (changesTheirs.hasNext()) changesTheirs.next() else null
                }
            }
        }

        return result.joinToString("\n").toMarkdown()
    }
}
