package ru.tasktracker.taskservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.taskservice.entity.TaskGroup

interface TaskGroupRepository : JpaRepository<TaskGroup, Long> {
    fun findTaskGroupById(id: Long): TaskGroup?
}