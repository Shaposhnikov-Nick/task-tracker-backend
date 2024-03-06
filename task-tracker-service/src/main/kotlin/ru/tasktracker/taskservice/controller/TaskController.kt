package ru.tasktracker.taskservice.controller


import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.dto.validation.ValidationGroups
import ru.tasktracker.taskservice.service.TaskService

@Validated
interface TaskController {
    fun getTasks(@AuthenticationPrincipal authUser: AuthenticatedUser): List<TaskDto>

    fun createTask(
        @AuthenticationPrincipal authUser: AuthenticatedUser,
        @RequestBody @Validated(ValidationGroups.Create::class) taskDto: TaskDto
    ): List<TaskDto>

    fun getTask(
        @AuthenticationPrincipal authUser: AuthenticatedUser,
        @PathVariable taskId: Long
    ): TaskDto

    fun updateTask(@AuthenticationPrincipal authUser: AuthenticatedUser): TaskDto
    fun deleteTask(@AuthenticationPrincipal authUser: AuthenticatedUser): List<TaskDto>


}


@RestController
@RequestMapping("tasks")
class TaskControllerImpl(
    val taskService: TaskService
) : TaskController {

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    override fun getTasks(authUser: AuthenticatedUser): List<TaskDto> {
        return taskService.getAllTasks(authUser.id)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    override fun createTask(authUser: AuthenticatedUser, taskDto: TaskDto): List<TaskDto> {
        return taskService.createTask(authUser, taskDto)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{taskId}")
    override fun getTask(authUser: AuthenticatedUser, taskId: Long): TaskDto {
        TODO("Not yet implemented")
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    override fun updateTask(authUser: AuthenticatedUser): TaskDto {
        TODO("Not yet implemented")
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    override fun deleteTask(authUser: AuthenticatedUser): List<TaskDto> {
        TODO("Not yet implemented")
    }

}
