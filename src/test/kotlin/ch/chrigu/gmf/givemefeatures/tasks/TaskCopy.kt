package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Markdown

fun Task.copy(id: TaskId = this.id, version: Long? = this.version, description: Markdown = this.description) = Task(id, version, name, description, status)
