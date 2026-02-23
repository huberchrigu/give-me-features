package ch.chrigu.gmf.tasks.web

import ch.chrigu.gmf.plugins.TaskReference
import ch.chrigu.gmf.plugins.TaskReferenceId
import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot
import ch.chrigu.gmf.tasks.TaskId

class PluginTask(id: TaskId, version: Long?) : TaskReference, AbstractAggregateRoot<TaskReferenceId>(id, version)
