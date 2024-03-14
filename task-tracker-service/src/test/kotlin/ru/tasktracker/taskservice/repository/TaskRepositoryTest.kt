package ru.tasktracker.taskservice.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import ru.tasktracker.taskservice.BaseTest
import ru.tasktracker.taskservice.entity.*


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest : BaseTest() {

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