package ch.chrigu.gmf.tasks.merger

import ch.chrigu.gmf.shared.history.AbstractMerger
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId

class TaskMerger : AbstractMerger<Task, TaskId>() {
    override fun getMergedAggregate(id: TaskId, version: Long, versions: MergingVersions<Task>) = with(versions) {
        Task(id, version, merge { name }, merge { description }, merge { status })
    }
}
