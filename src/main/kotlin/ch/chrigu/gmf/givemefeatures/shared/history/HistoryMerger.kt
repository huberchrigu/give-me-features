package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateRoot

class HistoryMerger<T : AggregateRoot<ID>, ID>(private val historyRepository: HistoryRepository<T, ID>) {
    suspend fun mergeWithVersion(id: ID, baseVersion: Long, mergeWithVersion: Long, mergeFunction: MergerDsl.(base: T, theirs: T) -> T): T {
        val theirs = historyRepository.findVersion(id, mergeWithVersion)!!
        val base = historyRepository.findVersion(id, baseVersion)!!
        return MergerDsl().mergeFunction(base, theirs)
    }

    class MergerDsl // TODO: Provide functions to facilitate merging
}