package ch.chrigu.gmf.tasks.web

import ch.chrigu.gfm.plugin.TaskReference
import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot
import ch.chrigu.gmf.tasks.TaskId

class PluginTask(id: TaskId, version: Long?) : TaskReference, AbstractAggregateRoot<TaskId>(id, version)
