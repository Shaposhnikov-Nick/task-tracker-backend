package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.repository.TaskRepository

interface TaskService {

    fun addTask(task: TaskDto): TaskDto

    fun getAllTasks(userId: Long): List<TaskDto>

}

@Service
class TaskServiceImpl(
    val taskRepository: TaskRepository
) : TaskService {
    override fun addTask(task: TaskDto): TaskDto {
        TODO("Not yet implemented")
    }

    override fun getAllTasks(userId: Long): List<TaskDto> {
        return taskRepository.findAllByUserId(userId).mapTo()
    }

}