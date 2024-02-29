package ru.tasktracker.adminservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.adminservice.entity.Role

interface RoleRepository: JpaRepository<Role, Long> {
    fun existsRoleByRole(role: String): Boolean
}