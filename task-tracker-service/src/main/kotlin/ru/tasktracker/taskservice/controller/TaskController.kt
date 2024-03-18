package ru.tasktracker.taskservice.controller


import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.taskservice.auth.AuthenticatedUser
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.dto.TaskGroupDto
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

    fun updateTask(
        @AuthenticationPrincipal authUser: AuthenticatedUser,
        @RequestBody @Validated(ValidationGroups.Update::class) taskDto: TaskDto
    ): TaskDto

    fun deleteTask(@AuthenticationPrincipal authUser: AuthenticatedUser, @PathVariable taskId: Long): List<TaskDto>

    fun addTaskGroup(@RequestBody @Validated(ValidationGroups.Create::class) taskGroupDto: TaskGroupDto): TaskGroupDto


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
        return taskService.getTask(authUser, taskId)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping
    override fun updateTask(authUser: AuthenticatedUser, taskDto: TaskDto): TaskDto {
        return taskService.updateTask(authUser, taskDto)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{taskId}")
    override fun deleteTask(authUser: AuthenticatedUser, taskId: Long): List<TaskDto> {
        return taskService.deleteTask(authUser, taskId)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/groups")
    override fun addTaskGroup(taskGroupDto: TaskGroupDto): TaskGroupDto {
        return taskService.addTaskGroup(taskGroupDto)
    }

}
