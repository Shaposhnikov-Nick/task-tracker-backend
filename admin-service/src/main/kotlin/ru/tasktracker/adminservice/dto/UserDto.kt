package ru.tasktracker.adminservice.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class ChangeUserRolesDto(

    @field:NotNull(message = "User id must not be null")
    val userId: Long,

    @field:NotEmpty(message = "List of roles must not be empty")
    val roles: Set<RoleDto>
)