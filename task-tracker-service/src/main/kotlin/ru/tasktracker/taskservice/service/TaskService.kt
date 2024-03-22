package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.dto.TaskGroupDto
import ru.tasktracker.taskservice.entity.Task
import ru.tasktracker.taskservice.entity.TaskGroup
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

    fun getTaskGroups(): List<TaskGroupDto>

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
    @Cacheable("tasks", unless = "#result.empty")
    override fun getAllTasks(userId: Long): List<TaskDto> {
        return taskRepository.findAllByUserId(userId).mapTo()
    }

    @Transactional
    override fun createTask(authUser: AuthenticatedUser, taskDto: TaskDto): List<TaskDto> {
        val task = taskDto.mapTo<Task>()
        val user = findUserById(authUser.id)

        user.addTask(task)
        userRepository.saveAndFlush(user)

        taskDto.groupId?.let {
            addTaskToGroup(authUser, taskDto.groupId, task.id!!, user)
        }

        return getAllTasks(authUser.id)
    }

    @Transactional(readOnly = true)
    override fun getTask(authUser: AuthenticatedUser, taskId: Long): TaskDto {
        val user = findUserById(authUser.id)
        return findTaskByUserAndId(user, taskId).mapTo()
    }

    @Transactional
    override fun updateTask(authUser: AuthenticatedUser, taskDto: TaskDto): TaskDto {
        val user = findUserById(authUser.id)
        val task = findTaskByUserAndId(user, taskDto.id!!)

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
        val user = findUserById(authUser.id)
        val deletedTask = findTaskByUserAndId(user, taskId)

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
        val deletedGroup = findTaskGroupById(groupId)
        val tasks = deletedGroup.tasks.toList()

        tasks.forEach {
            deletedGroup.removeTask(it)
        }

        taskRepository.saveAllAndFlush(tasks)
        taskGroupRepository.delete(deletedGroup)

        return "Task group $groupId deleted"
    }

    @Transactional(readOnly = true)
    override fun getTaskGroups(): List<TaskGroupDto> {
        return taskGroupRepository.findAll().mapTo()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun addTaskToGroup(authUser: AuthenticatedUser, groupId: Long, taskId: Long, user: User?): TaskDto {
        val group = findTaskGroupById(groupId)
        val userEntity = user ?: findUserById(authUser.id)
        val task = findTaskByUserAndId(userEntity, taskId)

        group.addTask(task)
        taskGroupRepository.saveAndFlush(group)

        return task.mapTo()
    }

    @Transactional
    override fun removeTaskFromGroup(authUser: AuthenticatedUser, groupId: Long, taskId: Long, user: User?): TaskDto {
        val group = findTaskGroupById(groupId)
        val userEntity = user ?: findUserById(authUser.id)
        val task = findTaskByUserAndId(userEntity, taskId)

        group.removeTask(task)
        taskGroupRepository.saveAndFlush(group)

        return task.mapTo()
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findUserById(userId) ?: throw UserException("User with id $userId not found")
    }

    private fun findTaskGroupById(groupId: Long): TaskGroup {
        return taskGroupRepository.findTaskGroupById(groupId)
            ?: throw TaskGroupException("Group with id $groupId not found")
    }

    private fun findTaskByUserAndId(user: User, taskId: Long): Task {
        return user.tasks.firstOrNull { it.id == taskId }
            ?: throw TaskException("Task with id $taskId for user ${user.id} not found")
    }

}