package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html

fun Task.copy(id: TaskId = this.id, version: Long? = this.version, description: Html = this.description) = Task(id, version, name, description, status)
