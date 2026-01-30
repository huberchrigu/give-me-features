package ch.chrigu.gmf.plugins.mongo

import ch.chrigu.gmf.plugins.PluginStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PluginStatusRepository : CoroutineCrudRepository<PluginStatus, String> {
    fun findByActive(active: Boolean): Flow<PluginStatus>
}
