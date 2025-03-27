package ch.chrigu.gmf.givemefeatures.shared.history

import org.springframework.dao.OptimisticLockingFailureException

class UpdateService<ID, T : Mergeable<*, T, ID>>(private val getAggregate: suspend (ID) -> T, private val saveAggregate: suspend (T) -> T) {
    /**
     * Update task without optimistic locking. Instead, get newest version first and apply changes on this.
     */
    suspend fun update(id: ID, version: Long, applyChange: suspend T.() -> T): T {
        val task = getAggregate(id).getVersion(version)
        val updatedTask = task.applyChange()
        return try {
            saveAggregate(updatedTask)
        } catch (e: OptimisticLockingFailureException) {
            mergeTask(updatedTask)
        }
    }

    private suspend fun mergeTask(task: T): T {
        val merged = getAggregate(task.id!!).mergeWith(task)
        return saveAggregate(merged)
    }
}