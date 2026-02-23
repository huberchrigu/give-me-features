package ch.chrigu.gmf.features.web

import ch.chrigu.gmf.plugins.FeatureReference
import ch.chrigu.gmf.plugins.ItemDefinition
import ch.chrigu.gmf.plugins.Plugin
import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.features.FeatureService
import ch.chrigu.gmf.plugins.ParentDefinition
import org.springframework.stereotype.Component

@Component
class FeatureDefinition(private val featureService: FeatureService) : ParentDefinition<FeatureReference> {

    override suspend fun resolve(id: String): FeatureReference {
        return featureService.getFeature(FeatureId(id)).asReference()
    }

    override val uriPrefix: String = "/features"

    override fun getItemDefinition(plugin: Plugin): ItemDefinition<FeatureReference, *>? {
        return plugin.touchpoints.featureItem
    }
}

fun Feature.asReference() = PluginFeature(id, version)