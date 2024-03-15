package ru.tasktracker.taskservice.controller


import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.tasktracker.taskservice.BaseTest


@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class TaskControllerImplTest: BaseTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun getAllTasks() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(task.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(task.description))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(task.user?.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(task.status.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].priority").value(task.priority.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].assigneeId").value(task.assigneeId))
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

    @Test
    fun deleteTask() {
    }
}