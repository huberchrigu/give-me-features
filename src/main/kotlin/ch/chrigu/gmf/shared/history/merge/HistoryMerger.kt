package ch.chrigu.gmf.shared.history.merge

import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.shared.history.AggregateMerger

class HistoryMerger<T : AggregateRoot<ID>, ID>(private val aggregateMerger: AggregateMerger<T, ID>, private val historyQuery: HistoryQuery<T, ID>) {
    /**
     * Use it like this:
     * ```
     * Merger().merge(repository.findById(id), newVersion)
     * ```
     * Then, if the repository version has the same version number as `newVersion`, keep the `newVersion`.
     * Otherwise, try to merge them based on history version for version number `newVersion.version`.
     *
     * @param mergingVersion This is the change that shall be merged on top of the [currentVersion].
     */
    suspend fun merge(currentVersion: T, mergingVersion: T): T {
        require(mergingVersion.version != null && currentVersion.version != null) { "Can only merge versioned tasks" }
        require(mergingVersion.version!! <= currentVersion.version!!) { "New version $mergingVersion should be equal or smaller than older version $currentVersion.version" }
        require(mergingVersion.id == currentVersion.id)
        if (mergingVersion.version == currentVersion.version) return mergingVersion
        val base = historyQuery.getVersion(currentVersion.id!!, mergingVersion.version!!)
        return aggregateMerger.merge(base, mergingVersion, currentVersion)
    }
}

fun interface HistoryQuery<T, ID> {
    suspend fun getVersion(id: ID, version: Long): T
}
