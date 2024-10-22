package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository) {
    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks.map { it.toString() })
    suspend fun newTask(task: Task) = taskRepository.save(task)
}
