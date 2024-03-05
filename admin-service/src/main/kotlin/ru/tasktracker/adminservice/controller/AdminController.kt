package ru.tasktracker.adminservice.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.adminservice.dto.ChangeUserRoleAction
import ru.tasktracker.adminservice.dto.ChangeUserRolesDto
import ru.tasktracker.adminservice.dto.RoleDto
import ru.tasktracker.adminservice.service.AdminService


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
    val adminService: AdminService
) : AdminController {

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("add-role")
    override fun addRole(roleDto: RoleDto): List<RoleDto> {
        return adminService.addRole(roleDto)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("add-user-role")
    override fun addUserRole(changedRoles: ChangeUserRolesDto): List<RoleDto> {
        return adminService.changeUserRole(changedRoles, ChangeUserRoleAction.ADD)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("remove-user-role")
    override fun removeUserRole(changedRoles: ChangeUserRolesDto): List<RoleDto> {
        return adminService.changeUserRole(changedRoles, ChangeUserRoleAction.REMOVE)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("block/{userId}")
    override fun blockUser(userId: Long) {
        adminService.blockUser(userId, true)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("unblock/{userId}")
    override fun unblockUser(userId: Long) {
        adminService.blockUser(userId, false)
    }

}