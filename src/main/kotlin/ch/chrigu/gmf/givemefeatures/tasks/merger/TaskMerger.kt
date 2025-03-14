package ch.chrigu.gmf.givemefeatures.tasks.merger

import ch.chrigu.gmf.givemefeatures.tasks.Task

class TaskMerger(private val base: Task, private val newVersion: Task, private val persistedNewVersion: Task) {
    init {
        require(base.id == newVersion.id && base.id == persistedNewVersion.id) { "ID must be the same for all versions" }
        require(base.version == newVersion.version) { "New version must be based on base version" }
        require(persistedNewVersion.version != null && base.version != null) { "All tasks should already be persisted" }
        require(persistedNewVersion.version > base.version) { "Persisted new version must be greater than base version" }
    }

    fun merge() = Task(base.id, merge { name }, merge { description }, merge { status }, persistedNewVersion.version)

    private fun <T : Any> merge(field: Task.() -> T): T {
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
