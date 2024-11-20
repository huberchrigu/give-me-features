package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.features.web.ui.asDetailView
import ch.chrigu.gmf.givemefeatures.features.web.ui.asListItem
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ResponseStatusException

@Controller
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService, private val taskService: TaskService) {
    @GetMapping
    fun listFeatures() = Rendering.view("features")
        .withFeatures(null)
        .build()

    @PostMapping(headers = [Hx.HEADER])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addFeature(@Valid newFeatureBody: NewFeatureBody): Rendering {
        val feature = featureService.newFeature(newFeatureBody.toFeature())
        return updateForFeature(feature)
    }

    @PostMapping("/{id}/tasks", headers = [Hx.HEADER])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addTaskToFeature(@PathVariable id: FeatureId, @Valid newTaskBody: NewTaskBody): Rendering {
        val feature = featureService.addTask(id, newTaskBody.toTask()) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return updateForFeature(feature)
    }

    @GetMapping("/{id}", headers = [Hx.HEADER])
    suspend fun getFeature(@PathVariable id: FeatureId): Rendering {
        val feature = featureService.getFeature(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return updateForFeature(feature)
    }

    @GetMapping("/{id}")
    suspend fun getFeaturePage(@PathVariable id: FeatureId): Rendering {
        val feature = featureService.getFeature(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return Rendering.view("features")
            .withFeatures(feature.id)
            .modelAttribute("feature", feature.asDetailView(taskService))
            .build()
    }

    private suspend fun updateForFeature(feature: Feature) = Rendering.view("update-feature")
        .withFeatures(feature.id)
        .modelAttribute("feature", feature.asDetailView(taskService))
        .build()

    private fun Rendering.Builder<*>.withFeatures(current: FeatureId?) = modelAttribute("features", featureService.getFeatures()
        .map { it.asListItem(current) })

    class NewFeatureBody(@field:NotEmpty private val name: String?, @field:NotEmpty private val description: String?) {
        fun toFeature() = Feature.describeNewFeature(name!!, description!!)
    }

    class NewTaskBody(@field:NotEmpty private val name: String?) {
        fun toTask() = Task.describeNewTask(name!!)
    }
}
