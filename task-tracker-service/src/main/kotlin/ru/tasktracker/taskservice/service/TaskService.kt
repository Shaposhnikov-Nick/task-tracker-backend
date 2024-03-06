package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.entity.Task
import ru.tasktracker.taskservice.exception.UserException
import ru.tasktracker.taskservice.repository.TaskRepository
import ru.tasktracker.taskservice.repository.UserRepository

interface TaskService {

    fun getAllTasks(userId: Long): List<TaskDto>

    fun createTask(authUser: AuthenticatedUser, taskDto: TaskDto): List<TaskDto>

}

@Service
class TaskServiceImpl(
    val taskRepository: TaskRepository,
    val userRepository: UserRepository
) : TaskService {

    @Transactional(readOnly = true)
    override fun getAllTasks(userId: Long): List<TaskDto> {
        return taskRepository.findAllByUserId(userId).mapTo()
    }

    @Transactional
    override fun createTask(authUser: AuthenticatedUser, taskDto: TaskDto): List<TaskDto> {
        val user = userRepository.findUserById(authUser.id)
            ?: throw UserException("User with id ${authUser.id} not found")
        user.addTask(taskDto.mapTo<Task>())
        return userRepository.saveAndFlush(user).tasks.mapTo<TaskDto>().toList()
    }

}