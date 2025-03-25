package ch.chrigu.gmf.givemefeatures.tasks.merger

import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

class TaskMerger(base: Task, newVersion: Task, persistedNewVersion: Task) : AbstractMerger<Task, TaskId>(base, newVersion, persistedNewVersion) {
    override fun mergeWith(id: TaskId, version: Long) = Task(id, merge { name }, merge { description }, merge { status }, version)
}
