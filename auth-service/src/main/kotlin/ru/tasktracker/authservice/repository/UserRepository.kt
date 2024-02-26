package ru.tasktracker.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.authservice.entity.User

interface UserRepository : JpaRepository<User, Long> {

    fun findUserById(id: Long): User?

    fun findUserByLoginAndBlockedIsFalse(login: String): User?
}