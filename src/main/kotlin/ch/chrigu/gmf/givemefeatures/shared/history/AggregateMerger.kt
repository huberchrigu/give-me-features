package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateRoot

interface AggregateMerger<T : AggregateRoot<ID>, ID> {
    /**
     * @param sharedVersion Base version is the last common version in both branches.
     */
    fun merge(sharedVersion: T, mergingVersion: T, currentVersion: T): T
}
