package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.features.FeatureUpdate
import ch.chrigu.gmf.givemefeatures.features.web.ui.FeatureListItem
import ch.chrigu.gmf.givemefeatures.features.web.ui.asDetailView
import ch.chrigu.gmf.givemefeatures.features.web.ui.asListItem
import ch.chrigu.gmf.givemefeatures.shared.Markdown
import ch.chrigu.gmf.givemefeatures.shared.web.FieldUpdate
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.result.view.Fragment
import org.springframework.web.reactive.result.view.FragmentsRendering
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ResponseStatusException

@Suppress("SpringMVCViewInspection")
@Controller
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService, private val taskService: TaskService) {
    @GetMapping
    fun listFeatures() = Rendering.view("features")
        .withFeatures()
        .build()

    @PostMapping(headers = [Hx.HEADER])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addFeature(@Valid newFeatureBody: NewFeatureBody): FragmentsRendering {
        val feature = featureService.newFeature(newFeatureBody.toFeature())
        return updateForFeature(feature)
    }

    @PostMapping("/{id}/tasks", headers = [Hx.HEADER])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addTaskToFeature(@PathVariable id: FeatureId, @RequestParam version: Long, @Valid newTaskBody: NewTaskBody): FragmentsRendering {
        val feature = featureService.addTask(id, version, newTaskBody.toTask()) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return updateForFeature(feature)
    }

    @GetMapping("/{id}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getFeatureUpdates(@PathVariable id: FeatureId) = featureService.getUpdates(id)
        .map {
            val featureDetail = it.asDetailView(taskService)
            ServerSentEvent.builder(Fragment.create("blocks/feature", mapOf("feature" to featureDetail))).build()
        }

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getFeatureListUpdate(@RequestParam current: FeatureId) = featureService.getAllUpdates()
        .map {
            ServerSentEvent.builder(listFragment(current)).build()
        }

    @GetMapping("/{id}/fields", produces = [MediaType.TEXT_EVENT_STREAM_VALUE]) // TODO: Test, same for task-edit
    suspend fun getFeatureFormUpdates(@PathVariable id: FeatureId, @RequestParam version: Long) = featureService.getDescriptionUpdates(id, version)
        .map {
            ServerSentEvent.builder(
                Fragment.create(
                    "atoms/updates", mapOf(
                        "fieldName" to "description",
                        "update" to FieldUpdate(
                            "/features/$id/description", it.description.toString(), // TODO: Do actual merge
                            it.description.toString()
                        )
                    )
                )
            ).build()
        }

    @PutMapping("/{id}/description", headers = [Hx.HEADER]) // TODO: Test, same for task-edit, should resolve conflict, too many "description" duplications
    fun mergeDescription(@PathVariable id: FeatureId, description: FeatureDescription): Rendering {
        return Rendering.view("atoms/richtext")
            .model(
                mapOf(
                    "fieldName" to "description",
                    "fieldTitle" to "Description",
                    "fieldValue" to description.description
                )
            )
            .build()
    }

    data class FeatureDescription(@field:NotNull val description: Markdown?)

    @GetMapping("/{id}", headers = [Hx.HEADER])
    suspend fun getFeature(@PathVariable id: FeatureId): FragmentsRendering {
        val feature = featureService.getFeature(id)
        return updateForFeature(feature)
    }

    @GetMapping("/{featureId}/edit", headers = [Hx.HEADER])
    suspend fun getFeatureEditForm(@PathVariable featureId: FeatureId) = Rendering.view("blocks/feature-edit")
        .modelAttribute("feature", featureService.getFeature(featureId))
        .build()

    @PatchMapping("/{featureId}", headers = [Hx.HEADER])
    suspend fun updateFeature(@PathVariable featureId: FeatureId, @RequestParam version: Long, @Valid updateFeature: UpdateFeatureDto) = Rendering.view("blocks/feature")
        .modelAttribute("feature", featureService.updateFeature(featureId, version, updateFeature.toDomain()).asDetailView(taskService))
        .build()

    @GetMapping("/{id}")
    suspend fun getFeaturePage(@PathVariable id: FeatureId): Rendering {
        val feature = featureService.getFeature(id)
        return Rendering.view("feature")
            .modelAttribute("feature", feature.asDetailView(taskService))
            .build()
    }

    /**
     * Creates both fragments with feature data for the feature list page.
     */
    private suspend fun updateForFeature(feature: Feature) = FragmentsRendering
        .withCollection(listOf(listFragment(feature.id)))
        .fragment(
            "blocks/feature",
            mapOf("feature" to feature.asDetailView(taskService))
        )
        .build()

    private fun getFeatureList(): Flow<FeatureListItem> = featureService.getFeatures().map { it.asListItem() }

    private fun Rendering.Builder<*>.withFeatures() = modelAttribute(
        "features", featureService.getFeatures()
            .map { it.asListItem() })

    private fun listFragment(current: FeatureId?) = Fragment.create("blocks/features", mapOf("features" to getFeatureList(), "current" to current))

    class NewFeatureBody(@field:NotEmpty private val name: String?, @field:NotEmpty private val description: String?) {
        fun toFeature() = Feature.describeNewFeature(name!!, Markdown(description!!))
    }

    class NewTaskBody(@field:NotEmpty private val name: String?) {
        fun toTask() = Task.describeNewTask(name!!)
    }
}

class UpdateFeatureDto(@field:NotEmpty private val name: String?, @field:NotNull private val description: Markdown?) {
    fun toDomain() = FeatureUpdate(name!!, description!!)
}
