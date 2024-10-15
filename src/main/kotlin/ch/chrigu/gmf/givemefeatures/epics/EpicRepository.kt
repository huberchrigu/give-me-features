package ch.chrigu.gmf.givemefeatures.epics

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface EpicRepository: CoroutineCrudRepository<Epic, UUID>