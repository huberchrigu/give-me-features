package ch.chrigu.gmf.features.web

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.features.FeatureService
import ch.chrigu.gmf.features.FeatureUpdate
import ch.chrigu.gmf.features.web.ui.FeatureListItem
import ch.chrigu.gmf.features.web.ui.asDetailView
import ch.chrigu.gmf.features.web.ui.asListItem
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.web.Hx
import ch.chrigu.gmf.shared.web.UpdateFragmentBuilder
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
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

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("SpringMVCViewInspection")
@Controller
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService, private val taskService: TaskService) {
    private val updateFragmentBuilder = UpdateFragmentBuilder<Feature>(
        "features",
        "name" to { name },
        "description" to { description })

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

    /**
     * Streams fragments of changed values.
     */
    @GetMapping("/{id}/fields", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun getFeatureFormUpdates(@PathVariable id: FeatureId, @RequestParam version: Long) = featureService.getUpdatesWithChangedValues(id, version)
        .flatMapConcat { updateFragmentBuilder.toFragments(it.first, it.second).asFlow() }

    @PutMapping("/{id}${UpdateFragmentBuilder.MERGE_URI}", headers = [Hx.HEADER])
    suspend fun mergeFeature(@PathVariable id: FeatureId, @Valid mergeFeatureBody: MergeFeatureBody, @RequestParam version: Long) = featureEditView(
        featureService.mergeWithVersion(id, mergeFeatureBody.name!!, mergeFeatureBody.description!!, version, mergeFeatureBody.newVersion!!)
    )

    @GetMapping("/{id}", headers = [Hx.HEADER])
    suspend fun getFeature(@PathVariable id: FeatureId): FragmentsRendering {
        val feature = featureService.getFeature(id)
        return updateForFeature(feature)
    }

    @GetMapping("/{featureId}/edit", headers = [Hx.HEADER])
    suspend fun getFeatureEditForm(@PathVariable featureId: FeatureId) = featureEditView(featureService.getFeature(featureId))

    @PatchMapping("/{featureId}", headers = [Hx.HEADER])
    suspend fun updateFeature(@PathVariable featureId: FeatureId, @RequestParam version: Long, @Valid updateFeature: UpdateFeatureBody) = Rendering.view("blocks/feature")
        .modelAttribute("feature", featureService.updateFeature(featureId, version, updateFeature.toDomain()).asDetailView(taskService))
        .build()

    @GetMapping("/{id}")
    suspend fun getFeaturePage(@PathVariable id: FeatureId): Rendering {
        val feature = featureService.getFeature(id)
        return Rendering.view("feature")
            .modelAttribute("feature", feature.asDetailView(taskService))
            .build()
    }

    private fun featureEditView(feature: Feature) = Rendering.view("blocks/feature-edit")
        .modelAttribute("feature", feature)
        .build()

    /**
     * Creates both fragments with feature data for the feature list page.
     */
    private suspend fun updateForFeature(feature: Feature) = FragmentsRendering
        .fragments(listOf(listFragment(feature.id)))
        .fragment(
            "blocks/feature",
            mapOf("feature" to feature.asDetailView(taskService))
        )
        .build()

    private fun getFeatureList(): Flow<FeatureListItem> = featureService.getFeatures().map { it.asListItem() }

    private fun Rendering.Builder<*>.withFeatures() = modelAttribute(
        "features", featureService.getFeatures()
            .map { it.asListItem() })

    private fun listFragment(current: FeatureId?) = Fragment.create(
        "blocks/features",
        mapOf("features" to getFeatureList()) +
                if (current == null) emptyMap() else mapOf("current" to current)
    )

    class NewFeatureBody(@field:NotEmpty private val name: String?, @field:NotEmpty private val description: String?) {
        fun toFeature() = Feature.describeNewFeature(name!!, Markdown(description!!))
    }

    class UpdateFeatureBody(@field:NotEmpty private val name: String?, @field:NotNull private val description: Markdown?) {
        fun toDomain() = FeatureUpdate(name!!, description!!)
    }

    data class MergeFeatureBody(@field:NotEmpty val name: String?, @field:NotNull val description: Markdown?, @field:NotNull val newVersion: Long?)

    class NewTaskBody(@field:NotEmpty private val name: String?) {
        fun toTask() = Task.describeNewTask(name!!)
    }
}
