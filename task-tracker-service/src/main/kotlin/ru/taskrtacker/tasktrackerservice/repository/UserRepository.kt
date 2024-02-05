package ru.taskrtacker.tasktrackerservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.taskrtacker.tasktrackerservice.entity.User

interface UserRepository : JpaRepository<User, Long>