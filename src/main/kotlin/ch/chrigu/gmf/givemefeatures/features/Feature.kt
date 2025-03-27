package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.history.FeatureMerger
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.shared.history.Mergeable
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import org.springframework.data.annotation.Version

data class Feature(
    override val id: FeatureId?, val name: String, val description: Html, val tasks: List<TaskId>,
    @field:Version override val version: Long? = null, override val history: History<FeatureSnapshot> = History()
) : Mergeable<FeatureSnapshot, Feature, FeatureId> {
    fun planNewTask(task: Task) = newVersion(copy(tasks = tasks + task.id!!))

    override fun withSnapshot(snapshot: FeatureSnapshot) = Feature(id, snapshot.name, snapshot.description, snapshot.tasks, snapshot.version, history)
    override fun getCurrent() = FeatureSnapshot(name, description, tasks, version!!)
    override fun withHistory(history: History<FeatureSnapshot>) = copy(history = history)
    override fun getMerger(base: Feature, newVersion: Feature, oldVersion: Feature) = FeatureMerger(base, newVersion, oldVersion)

    companion object {
        fun describeNewFeature(name: String, description: Html) = Feature(null, name, description, emptyList())
    }
}

@JvmInline
value class FeatureId(private val id: String) {
    override fun toString(): String = id
}
