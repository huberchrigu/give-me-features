package ch.chrigu.gmf.testing.plugin

import ch.chrigu.gmf.plugins.Plugin
import ch.chrigu.gmf.plugins.PluginId
import ch.chrigu.gmf.testing.persistence.TestDefinitionRepository
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConditionalOnClass(Plugin::class)
@ConditionalOnBean(TestDefinitionRepository::class)
class TestingPluginAutoConfiguration {
    @Bean
    fun testingPlugin(testDefinitionRepository: TestDefinitionRepository) = Plugin(
        PluginId("testing"), "Testing", TestingTouchpointFactory(testDefinitionRepository).create()
    )
}
