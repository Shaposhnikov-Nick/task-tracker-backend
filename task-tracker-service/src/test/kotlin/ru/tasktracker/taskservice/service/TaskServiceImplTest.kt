package ru.tasktracker.taskservice.service

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import ru.tasktracker.taskservice.entity.*
import ru.tasktracker.taskservice.repository.TaskRepository
import ru.tasktracker.taskservice.repository.UserRepository
import java.time.LocalDateTime


@Testcontainers
@SpringBootTest
class TaskServiceImplTest {
    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var userRepository: UserRepository

    lateinit var user: User

    companion object {

        @Container
        @ServiceConnection
        val container = PostgreSQLContainer("postgres:15")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/")

        @JvmStatic
        @BeforeAll
        fun init() {
            container.execInContainer("insert into users values (1,22222222,tests,false,false,1,2024-03-04 17:02:42.778983,2024-03-04 17:02:42.778983,none)")
        }
    }

//    @BeforeEach
//    fun init() {
//        val newUser = User(
//            "test",
//            "test",
//            false,
//            false,
//            mutableSetOf(),
//            UserProfile(null, "name", "lastname", LocalDateTime.now(), "test@test.ru", null, null)
//        )
//        user = userRepository.saveAndFlush(newUser)
//    }

    @Test
    fun getAllTasks() {
        val tasks = taskRepository.findAllByUserId(1)
        tasks.size
    }

    @Test
    fun createTask() {
        val tasks = taskRepository.findAllByUserId(1)

        val task = Task(
            title = "Test title",
            description = "Test Description",
            status = TaskStatus.TODO,
            priority = TaskPriority.LOW,
            deadLine = LocalDateTime.now().plusDays(1),
            assigneeId = 1,
            group = null,
            taskComments = mutableSetOf(),
            parentId = null
        )

        taskRepository.saveAndFlush(task)

        val tasks2 = taskRepository.findAllByUserId(1)
        tasks2.size
    }

    @Test
    fun getTask() {
    }

    @Test
    fun updateTask() {
    }
}