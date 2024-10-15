package ch.chrigu.gmf.givemefeatures.tasks

import org.springframework.data.repository.CrudRepository
import java.util.*

interface TaskRepository : CrudRepository<Task, UUID>