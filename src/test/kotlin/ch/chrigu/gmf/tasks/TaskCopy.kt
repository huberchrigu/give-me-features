package ch.chrigu.gmf.tasks

import ch.chrigu.gmf.shared.markdown.Markdown

fun Task.copy(id: TaskId = this.id, version: Long? = this.version, description: Markdown = this.description) = Task(id, version, name, description, status)
