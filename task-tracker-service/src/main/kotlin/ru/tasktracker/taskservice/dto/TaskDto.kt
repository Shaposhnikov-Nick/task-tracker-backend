package ru.tasktracker.taskservice.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import cz.encircled.skom.Convertable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import ru.tasktracker.taskservice.dto.validation.ValidationGroups
import ru.tasktracker.taskservice.entity.*
import java.time.LocalDateTime


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TaskDto(

    @field:Null(groups = [ValidationGroups.Create::class], message = "Id must be null")
    @field:NotNull(groups = [ValidationGroups.Update::class], message = "Id can't be null")
    val id: Long? = null,

    @field:NotBlank(
        groups = [ValidationGroups.Create::class, ValidationGroups.Update::class],
        message = "Title must not be blank"
    )
    val title: String,

    @field:NotBlank(
        groups = [ValidationGroups.Create::class, ValidationGroups.Update::class],
        message = "Description must not be blank"
    )
    val description: String,
    val group: TaskGroup? = null,
    val userId: Long? = null,
    val parentId: Long? = null,

    @field:NotNull(
        groups = [ValidationGroups.Create::class, ValidationGroups.Update::class],
        message = "Status must not be blank"
    )
    val status: TaskStatus,

    @field:NotNull(
        groups = [ValidationGroups.Create::class, ValidationGroups.Update::class],
        message = "Priority must not be blank"
    )
    val priority: TaskPriority,

    @field:NotNull(
        groups = [ValidationGroups.Create::class, ValidationGroups.Update::class],
        message = "Deadline must not be null"
    )
    val deadLine: LocalDateTime,

    @field:NotNull(
        groups = [ValidationGroups.Create::class, ValidationGroups.Update::class],
        message = "Assignee id must not be null"
    )
    val assigneeId: Long,
    val taskComments: Set<TaskComment> = mutableSetOf(),
    var createdDate: LocalDateTime? = null,
    var updateDate: LocalDateTime? = null,
    var updatedBy: String? = null
) : Convertable