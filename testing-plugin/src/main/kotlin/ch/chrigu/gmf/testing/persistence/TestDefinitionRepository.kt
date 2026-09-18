package ch.chrigu.gmf.testing.persistence

import ch.chrigu.gmf.plugins.PluginRepository
import ch.chrigu.gmf.testing.TestDefinition
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TestDefinitionRepository : CoroutineCrudRepository<TestDefinition, String>

fun <ID : Any> TestDefinitionRepository.toPluginRepository(): PluginRepository<TestDefinition, ID> =
    object : PluginRepository<TestDefinition, ID> {
        override suspend fun save(pluginEntity: TestDefinition): TestDefinition {
            return this@toPluginRepository.save(pluginEntity)
        }

        override suspend fun findById(id: ID): TestDefinition? {
            return this@toPluginRepository.findById(id.toString())
        }
    }