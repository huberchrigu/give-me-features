package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.Snapshot
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

data class FeatureSnapshot(val name: String, val description: Html, val tasks: List<TaskId>, override val version: Long) : Snapshot<FeatureSnapshot> {
    override fun withVersion(version: Long): FeatureSnapshot {
        return copy(version = version)
    }
}
