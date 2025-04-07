package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

fun Feature.copy(
    id: FeatureId = this.id,
    name: String = this.name,
    description: Html = this.description,
    tasks: List<TaskId> = this.tasks,
    version: Long? = this.version
) = Feature(id, name, description, tasks, version)