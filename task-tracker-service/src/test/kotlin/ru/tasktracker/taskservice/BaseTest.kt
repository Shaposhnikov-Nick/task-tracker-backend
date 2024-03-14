package ru.tasktracker.taskservice

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.security.core.context.SecurityContextHolder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.auth.JwtAuthentication
import ru.tasktracker.taskservice.entity.*
import ru.tasktracker.taskservice.repository.TaskRepository
import ru.tasktracker.taskservice.repository.UserRepository
import java.time.LocalDateTime


@Testcontainers
abstract class BaseTest {

    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var userRepository: UserRepository

    lateinit var user: User

    lateinit var task: Task

    lateinit var authUser: AuthenticatedUser

    companion object {

        @Container
        @ServiceConnection
        val container = PostgreSQLContainer("postgres:15")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/")

    }

    @BeforeEach
    open fun init() {
        val newUser = User(
            "test",
            "test",
            false,
            false,
            mutableSetOf(Role("ADMIN")),
            UserProfile(null, "name", "lastname", LocalDateTime.now(), "test@test.ru", null, null)
        )
        user = userRepository.saveAndFlush(newUser)

        val newTask = Task(
            title = "Test title",
            description = "Test Description",
            status = TaskStatus.TODO,
            priority = TaskPriority.LOW,
            deadLine = LocalDateTime.now().plusDays(1),
            assigneeId = user.id!!,
            group = null,
            user = user,
            taskComments = mutableSetOf(),
            parentId = null
        )
        task = taskRepository.saveAndFlush(newTask)

        val auth = JwtAuthentication(true)
        auth.user = AuthenticatedUser(user.id!!, user.login, true)
        SecurityContextHolder.getContext().authentication = auth
        authUser = SecurityContextHolder.getContext().authentication.principal as AuthenticatedUser
    }

    @AfterEach
    open fun destroy() {
        userRepository.deleteById(user.id!!)
    }

}