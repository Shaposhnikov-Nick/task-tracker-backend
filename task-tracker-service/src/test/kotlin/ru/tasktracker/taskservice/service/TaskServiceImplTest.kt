package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.tasktracker.taskservice.BaseTest


@SpringBootTest
class TaskServiceImplTest : BaseTest() {

    @Autowired
    lateinit var taskService: TaskService

    @Test
    fun getAllTasks() {
        val tasks = taskService.getAllTasks(authUser.id)

        assertThat(tasks)
            .isNotNull()
            .isNotEmpty()
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                "id",
                "createdDate",
                "updateDate",
                "updatedBy",
                "deadLine"
            )
            .contains(task.mapTo())
    }

    @Test
    fun createTask() {
    }

    @Test
    fun getTask() {
    }

    @Test
    fun updateTask() {
    }
}