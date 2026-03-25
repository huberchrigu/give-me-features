package ch.chrigu.gmf.features.web

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.features.FeatureService
import ch.chrigu.gmf.plugins.FeatureReference
import ch.chrigu.gmf.plugins.FeatureReferenceId
import ch.chrigu.gmf.plugins.ParentDefinition
import ch.chrigu.gmf.plugins.Plugin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class FeatureDefinition(private val featureService: FeatureService) : ParentDefinition<FeatureReference, FeatureReferenceId> {

    override suspend fun resolve(id: String): FeatureReference {
        return featureService.getFeature(FeatureId(id)).asReference()
    }

    override val uriPrefix: String = "/features"

    override fun getItemDefinition(plugin: Plugin) = plugin.touchpoints.featureItem

    override val changes: Flow<FeatureReference> = featureService.getAllUpdates(false).map { it.asReference() }
}

fun Feature.asReference() = PluginFeature(id, version)