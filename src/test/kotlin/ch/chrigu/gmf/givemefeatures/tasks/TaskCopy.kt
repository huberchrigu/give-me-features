package ch.chrigu.gmf.givemefeatures.tasks

fun Task.copy(id: TaskId? = this.id) = Task(id, version, name, description, status)