package ru.tasktracker.taskservice.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import ru.tasktracker.taskservice.entity.*
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class TaskDto(
    val id: Long,
    val title: String,
    val description: String,
    val group: TaskGroup?,
    val userId: Long,
    val parentId: Long?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val deadLine: LocalDateTime,
    val assigneeId: Long,
    val taskComments: Set<TaskComment> = mutableSetOf(),
    var createdDate: LocalDateTime? = null,
    var updateDate: LocalDateTime? = null,
    var updatedBy: String? = null
)