package ch.chrigu.gmf.givemefeatures.tasks.merger

import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

class TaskMerger : AbstractMerger<Task, TaskId>() {
    override fun getMergedAggregate(id: TaskId, version: Long, versions: MergingVersions<Task>) = with(versions) {
        Task(id, version, merge { name }, merge { description }, merge { status })
    }
}
