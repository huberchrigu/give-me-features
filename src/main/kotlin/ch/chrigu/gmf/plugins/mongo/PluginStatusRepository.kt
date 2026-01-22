package ch.chrigu.gmf.plugins.mongo

import ch.chrigu.gmf.plugins.PluginStatus
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PluginStatusRepository : CoroutineCrudRepository<PluginStatus, String>
