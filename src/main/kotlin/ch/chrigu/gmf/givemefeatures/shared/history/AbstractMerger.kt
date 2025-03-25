package ch.chrigu.gmf.givemefeatures.shared.history

abstract class AbstractMerger<T : WithHistory<*, T, ID>, ID>(private val base: T, private val newVersion: T, private val persistedNewVersion: T) {
    init {
        require(base.id == newVersion.id && base.id == persistedNewVersion.id) { "ID must be the same for all versions" }
        require(base.version == newVersion.version) { "New version must be based on base version" }
        require(persistedNewVersion.version != null && base.version != null) { "All tasks should already be persisted" }
        require(persistedNewVersion.version!! > base.version!!) { "Persisted new version must be greater than base version" }
    }

    fun merge() = mergeWith(base.id!!, persistedNewVersion.version!!)

    abstract fun mergeWith(id: ID, version: Long): T

    protected fun <F : Any> merge(field: T.() -> F): F {
        val baseField = base.field()
        val newField = newVersion.field()
        val persistedNewField = persistedNewVersion.field()
        if (baseField == newField) return persistedNewField
        if (baseField == persistedNewField) return newField
        throw MergeFailedException(baseField, newField, persistedNewField)
    }

    class MergeFailedException(baseField: Any, newField: Any, persistedNewField: Any) :
        IllegalArgumentException("Cannot merge new value '$newField' and persisted value '$persistedNewField', both are different than '$baseField'")
}
