package ru.tasktracker.adminservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.adminservice.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findUserById(id: Long): User?
}