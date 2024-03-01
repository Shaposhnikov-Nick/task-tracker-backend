package ru.tasktracker.taskservice.controller


import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.service.TaskService


interface TaskController {
    fun getTasks(@AuthenticationPrincipal authUser: AuthenticatedUser): List<TaskDto>
}


@RestController
@RequestMapping("task")
class TaskControllerImpl(
    val taskService: TaskService
) : TaskController {

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    override fun getTasks(authUser: AuthenticatedUser): List<TaskDto> {
        return taskService.getAllTasks(authUser.id)
    }

}
