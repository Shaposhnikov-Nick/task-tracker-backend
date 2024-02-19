package ru.tasktracker.taskservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.taskservice.entity.Task


interface TaskRepository : JpaRepository<Task, Long>