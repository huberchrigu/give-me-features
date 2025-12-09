package ch.chrigu.gmf.shared.history

import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.markdown.MarkdownDiff

class HistoryMerger<T : AggregateRoot<ID>, ID>(private val historyRepository: HistoryRepository<T, ID>) {
    suspend fun mergeWithVersion(id: ID, baseVersion: Long, mergeWithVersion: Long, mergeFunction: MergerDsl<T>.() -> T): T {
        val theirs = historyRepository.findVersion(id, mergeWithVersion)!!
        val base = historyRepository.findVersion(id, baseVersion)!!
        return MergerDsl(base, theirs).mergeFunction()
    }

    /**
     * @param base The common base version.
     * @param theirs The existing version, newer than the base version.
     */
    class MergerDsl<T : AggregateRoot<*>>(private val base: T, val theirs: T) {
        fun String.merge(getter: T.() -> String) = MarkdownDiff.simpleMerge(base.getter(), this, theirs.getter())
        fun Markdown.merge(getter: T.() -> Markdown) = MarkdownDiff.merge3(base.getter(), this, theirs.getter())
    }
}