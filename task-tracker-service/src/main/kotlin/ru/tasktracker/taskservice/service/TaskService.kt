package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.dto.TaskGroupDto
import ru.tasktracker.taskservice.entity.Task
import ru.tasktracker.taskservice.entity.User
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

    fun deleteTask(authUser: AuthenticatedUser, taskId: Long): List<TaskDto>

    fun addTaskGroup(taskGroupDto: TaskGroupDto): TaskGroupDto

    fun deleteTaskGroup(groupId: Long): String

    fun addTaskToGroup(authUser: AuthenticatedUser, groupId: Long, taskId: Long, user: User? = null): TaskDto

    fun removeTaskFromGroup(authUser: AuthenticatedUser, groupId: Long, taskId: Long, user: User? = null): TaskDto

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
        val task = taskDto.mapTo<Task>()

        val user = userRepository.findUserById(authUser.id)
            ?: throw UserException("User with id ${authUser.id} not found")

        user.addTask(task)
        userRepository.saveAndFlush(user)

        taskDto.groupId?.let {
            addTaskToGroup(authUser, taskDto.groupId, task.id!!, user)
        }

        return getAllTasks(authUser.id)
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
    override fun deleteTask(authUser: AuthenticatedUser, taskId: Long): List<TaskDto> {
        val user = userRepository.findUserById(authUser.id)
            ?: throw UserException("User with id ${authUser.id} not found")

        val deletedTask = user.tasks.firstOrNull { it.id == taskId }
            ?: throw TaskException("Task with id $taskId for user ${authUser.id} not found")

        deletedTask.group?.let {
            removeTaskFromGroup(authUser, deletedTask.group?.id!!, taskId, user)
        }

        user.removeTask(deletedTask)
        userRepository.saveAndFlush(user)
        return getAllTasks(user.id!!)
    }

    @Transactional
    override fun addTaskGroup(taskGroupDto: TaskGroupDto): TaskGroupDto {
        return taskGroupRepository.saveAndFlush(taskGroupDto.mapTo()).mapTo()
    }

    @Transactional
    override fun deleteTaskGroup(groupId: Long): String {
        val deletedGroup = taskGroupRepository.findTaskGroupById(groupId)
            ?: throw TaskGroupException("Group with id $groupId not found")

        val tasks = deletedGroup.tasks.toList()

        tasks.forEach {
            deletedGroup.removeTask(it)
        }

        taskRepository.saveAllAndFlush(tasks)
        taskGroupRepository.delete(deletedGroup)

        return "Task group $groupId deleted"
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun addTaskToGroup(authUser: AuthenticatedUser, groupId: Long, taskId: Long, user: User?): TaskDto {
        val group = taskGroupRepository.findTaskGroupById(groupId)
            ?: throw TaskGroupException("Group with id $groupId not found")

        val userEntity = user ?: userRepository.findUserById(authUser.id)
        ?: throw UserException("User with id ${authUser.id} not found")

        val task = userEntity.tasks.firstOrNull { it.id == taskId }
            ?: throw TaskException("Task with id $taskId for user ${authUser.id} not found")

        group.addTask(task)
        taskGroupRepository.saveAndFlush(group)

        return task.mapTo()

    }

    @Transactional
    override fun removeTaskFromGroup(authUser: AuthenticatedUser, groupId: Long, taskId: Long, user: User?): TaskDto {
        val group = taskGroupRepository.findTaskGroupById(groupId)
            ?: throw TaskGroupException("Group with id $groupId not found")

        val userEntity = user ?: userRepository.findUserById(authUser.id)
        ?: throw UserException("User with id ${authUser.id} not found")

        val task = userEntity.tasks.firstOrNull { it.id == taskId }
            ?: throw TaskException("Task with id $taskId for user ${authUser.id} not found")

        group.removeTask(task)
        taskGroupRepository.saveAndFlush(group)

        return task.mapTo()
    }

}