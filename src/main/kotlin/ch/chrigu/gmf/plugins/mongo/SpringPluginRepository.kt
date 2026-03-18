package ch.chrigu.gmf.plugins.mongo

import ch.chrigu.gmf.plugins.PluginRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

fun <T : Any, ID : Any> CoroutineCrudRepository<T, String>.toPluginRepository() = SpringPluginRepository<T, ID>(this)
class SpringPluginRepository<T : Any, ID : Any>(private val delegate: CoroutineCrudRepository<T, String>) : PluginRepository<T, ID> {
    override suspend fun save(pluginEntity: T): T {
        return delegate.save(pluginEntity)
    }

    override suspend fun findById(id: ID): T? {
        return delegate.findById(id.toString())
    }
}