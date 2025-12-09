package ch.chrigu.gmf.epics

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface EpicRepository: CoroutineCrudRepository<Epic, UUID>