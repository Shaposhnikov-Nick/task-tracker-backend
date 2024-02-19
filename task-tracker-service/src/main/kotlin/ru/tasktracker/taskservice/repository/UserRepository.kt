package ru.tasktracker.taskservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.taskservice.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findUserById(id: Long): User?
}