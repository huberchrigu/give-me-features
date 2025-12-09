package ch.chrigu.gmf.features

import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.tasks.TaskId

fun Feature.copy(
    id: FeatureId = this.id,
    name: String = this.name,
    description: Markdown = this.description,
    tasks: List<TaskId> = this.tasks,
    version: Long? = this.version
) = Feature(id, name, description, tasks, version)
