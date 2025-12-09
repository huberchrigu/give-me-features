package ch.chrigu.gmf.shared.history

import ch.chrigu.gmf.shared.aggregates.AggregateRoot

abstract class AbstractMerger<T : AggregateRoot<ID>, ID> : AggregateMerger<T, ID> {

    /**
     * The merged aggregate has the same version as the last one in the history. This is as intended, because Spring Data will forward the version when saving the aggregate.
     */
    override fun merge(sharedVersion: T, mergingVersion: T, currentVersion: T): T {
        require(sharedVersion.id == mergingVersion.id && sharedVersion.id == currentVersion.id) { "ID must be the same for all versions" }
        require(sharedVersion.version == mergingVersion.version) { "New version must be based on base version" }
        require(currentVersion.version != null && sharedVersion.version != null) { "All tasks should already be persisted" }
        require(currentVersion.version!! > sharedVersion.version!!) { "Persisted new version must be greater than base version" }
        return getMergedAggregate(sharedVersion.id!!, currentVersion.version!!, MergingVersions(sharedVersion, mergingVersion, currentVersion))
    }

    abstract fun getMergedAggregate(id: ID, version: Long, versions: MergingVersions<T>): T

    class MergingVersions<T>(private val sharedVersion: T, private val mergingVersion: T, private val currentVersion: T) {
        fun <F : Any> merge(field: T.() -> F): F {
            val sharedFieldValue = sharedVersion.field()
            val mergingFieldValue = mergingVersion.field()
            val currentFieldValue = currentVersion.field()
            if (sharedFieldValue == mergingFieldValue) return currentFieldValue
            if (sharedFieldValue == currentFieldValue) return mergingFieldValue
            throw MergeFailedException(sharedFieldValue, mergingFieldValue, currentFieldValue)
        }
    }

    class MergeFailedException(baseField: Any, newField: Any, persistedNewField: Any) :
        IllegalArgumentException("Cannot merge new value '$newField' and persisted value '$persistedNewField', both are different than '$baseField'")
}
