package ch.chrigu.gmf.plugins

interface PluginRepository<T : Any, ID> {
    suspend fun save(pluginEntity: T): T
    suspend fun findById(id: ID): T?
}
