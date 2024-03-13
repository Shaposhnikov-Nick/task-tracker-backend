package ru.tasktracker.taskservice.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import ru.tasktracker.taskservice.entity.*
import java.time.LocalDateTime

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var userRepository: UserRepository

    lateinit var user: User

    lateinit var task: Task

    companion object {

        @Container
        @ServiceConnection
        val container = PostgreSQLContainer("postgres:15")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/")
    }

    @BeforeEach
    fun init() {
        val newUser = User(
            "test",
            "test",
            false,
            false,
            mutableSetOf(),
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
    }

    @AfterEach
    fun destroy() {
        taskRepository.delete(task)
        userRepository.delete(user)
    }

    @Test
    fun connectionEstablished() {
        assertThat(container.isCreated()).isTrue()
        assertThat(container.isRunning()).isTrue()
    }

    @Test
    fun `Create and get task`() {
        val allUserTasks = taskRepository.findAllByUserId(user.id!!)

        assertThat(taskRepository.findById(task.id!!)).isNotNull
        assertThat(allUserTasks).contains(task)
    }

    @Test
    fun updateTask() {
        task.status = TaskStatus.IN_PROGRESS
        task.priority = TaskPriority.HIGH
        task.description = "new description"

        val updatedTask = taskRepository.saveAndFlush(task)

        assertThat(updatedTask).isNotNull
        assertThat(updatedTask.status).isEqualTo(TaskStatus.IN_PROGRESS)
        assertThat(updatedTask.priority).isEqualTo(TaskPriority.HIGH)
        assertThat(updatedTask.description).isEqualTo("new description")
    }

}