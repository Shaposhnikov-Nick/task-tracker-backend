package ru.tasktracker.taskservice.controller


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.service.TaskService


interface TaskController {
    fun getTasks(@PathVariable userId: Long): List<TaskDto>
}


@RestController
@RequestMapping("task")
class TaskControllerImpl(
    val taskService: TaskService
) : TaskController {

    @GetMapping("/{userId}")
    override fun getTasks(userId: Long): List<TaskDto> {
        return taskService.getAllTasks(userId)
    }

}
