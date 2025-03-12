package ru.tasktracker.taskservice.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.taskservice.dto.ChangeUserRoleAction
import ru.tasktracker.taskservice.dto.ChangeUserRolesDto
import ru.tasktracker.taskservice.dto.RoleDto
import ru.tasktracker.taskservice.service.UserService


@Validated
interface AdminController {
    fun addRole(@RequestBody @Valid roleDto: RoleDto): List<RoleDto>
    fun addUserRole(@RequestBody @Valid changedRoles: ChangeUserRolesDto): List<RoleDto>
    fun removeUserRole(@RequestBody @Valid changedRoles: ChangeUserRolesDto): List<RoleDto>
    fun blockUser(@PathVariable userId: Long)
    fun unblockUser(@PathVariable userId: Long)
}

@RestController
@RequestMapping("admin")
class AdminControllerImpl(
    val userService: UserService
) : AdminController {

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("add-role")
    override fun addRole(roleDto: RoleDto): List<RoleDto> {
        return userService.addRole(roleDto)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("add-user-role")
    override fun addUserRole(changedRoles: ChangeUserRolesDto): List<RoleDto> {
        return userService.changeUserRole(changedRoles, ChangeUserRoleAction.ADD)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("remove-user-role")
    override fun removeUserRole(changedRoles: ChangeUserRolesDto): List<RoleDto> {
        return userService.changeUserRole(changedRoles, ChangeUserRoleAction.REMOVE)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("block/{userId}")
    override fun blockUser(userId: Long) {
        userService.blockUser(userId, true)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("unblock/{userId}")
    override fun unblockUser(userId: Long) {
        userService.blockUser(userId, false)
    }

}