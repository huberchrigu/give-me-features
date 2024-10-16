package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService) {
    @GetMapping
    fun listFeatures() = Rendering.view("features")
        .withFeatures()
        .build()

    @PostMapping
    suspend fun addFeature(@Valid newFeatureBody: NewFeatureBody): Rendering {
        featureService.newFeature(newFeatureBody.toFeature())
        return Rendering.view("features :: features")
            .withFeatures()
            .build()
    }

    @GetMapping("/{id}")
    suspend fun getFeature(@PathVariable id: FeatureId): Nothing = TODO()

    private fun Rendering.Builder<*>.withFeatures() = modelAttribute("features", featureService.getFeatures()
        .map { it.asListItem() })

    private fun Feature.asListItem() = FeatureListItem(name, "/features/$id")

    class FeatureListItem(val name: String, val link: String) // TODO: Should mark new items in list
    class NewFeatureBody(@field:NotEmpty private val name: String?, @field:NotEmpty private val description: String?) {
        fun toFeature() = Feature.describeNewFeature(name!!, description!!)
    }
}