package ru.tasktracker.taskservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.tasktracker.taskservice.entity.Role

interface RoleRepository : JpaRepository<Role, Long> {

    fun existsRoleByName(name: String): Boolean

    @Query(value = """
        select r from Role r
        join r.users ur
        where ur.id = :userId
    """
    )
    fun findRolesByUserId(@Param("userId") userId: Long): List<Role>
}