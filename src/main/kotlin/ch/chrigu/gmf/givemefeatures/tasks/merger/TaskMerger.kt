package ch.chrigu.gmf.givemefeatures.tasks.merger

import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.history.TaskSnapshot

class TaskMerger(base: Task, newVersion: Task, persistedNewVersion: Task) : AbstractMerger<TaskSnapshot, Task, TaskId>(base, newVersion, persistedNewVersion) {
    override fun mergeWith(id: TaskId, version: Long, history: History<TaskSnapshot>) = Task(id, merge { name }, merge { description }, merge { status }, version, history)
}
