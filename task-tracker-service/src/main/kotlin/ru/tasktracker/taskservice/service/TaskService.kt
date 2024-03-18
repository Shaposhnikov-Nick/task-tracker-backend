package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.dto.TaskGroupDto
import ru.tasktracker.taskservice.entity.Task
import ru.tasktracker.taskservice.exception.TaskException
import ru.tasktracker.taskservice.exception.TaskGroupException
import ru.tasktracker.taskservice.exception.UserException
import ru.tasktracker.taskservice.repository.TaskGroupRepository
import ru.tasktracker.taskservice.repository.TaskRepository
import ru.tasktracker.taskservice.repository.UserRepository

interface TaskService {

    fun getAllTasks(userId: Long): List<TaskDto>

    fun createTask(authUser: AuthenticatedUser, taskDto: TaskDto): List<TaskDto>

    fun getTask(authUser: AuthenticatedUser, taskId: Long): TaskDto

    fun updateTask(authUser: AuthenticatedUser, taskDto: TaskDto): TaskDto

    fun addTaskGroup(taskGroupDto: TaskGroupDto): TaskGroupDto

}

@Service
class TaskServiceImpl(
    val taskRepository: TaskRepository,
    val userRepository: UserRepository,
    val taskGroupRepository: TaskGroupRepository
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

    @Transactional(readOnly = true)
    override fun getTask(authUser: AuthenticatedUser, taskId: Long): TaskDto {
        val user = userRepository.findUserById(authUser.id)
            ?: throw UserException("User with id ${authUser.id} not found")

        return user.tasks.firstOrNull { it.id == taskId }?.mapTo<TaskDto>()
            ?: throw TaskException("Task with id $taskId not found")
    }

    @Transactional
    override fun updateTask(authUser: AuthenticatedUser, taskDto: TaskDto): TaskDto {
        val user = userRepository.findUserById(authUser.id)
            ?: throw UserException("User with id ${authUser.id} not found")

        val task = user.tasks.firstOrNull { it.id == taskDto.id }
            ?: throw TaskException("Task with id ${taskDto.id} for user ${authUser.id} not found")

        task.title = taskDto.title
        task.description = taskDto.description
        task.status = taskDto.status
        task.priority = taskDto.priority
        task.deadLine = taskDto.deadLine
        task.assigneeId = taskDto.assigneeId

        return taskRepository.saveAndFlush(task).mapTo()
    }

    @Transactional
    override fun addTaskGroup(taskGroupDto: TaskGroupDto): TaskGroupDto {
        return taskGroupRepository.saveAndFlush(taskGroupDto.mapTo()).mapTo()
    }

}