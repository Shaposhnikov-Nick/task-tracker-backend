package ru.tasktracker.adminservice.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.adminservice.dto.RoleDto
import ru.tasktracker.adminservice.service.AdminService


@Validated
interface AdminController {

    fun addRole(@RequestBody @Valid roleDto: RoleDto): List<RoleDto>

    fun changeUserRole()

    fun blockUser()

    fun unblockUser()
}

@RestController
@RequestMapping("admin")
class AdminControllerImpl(
    val adminService: AdminService
) : AdminController {

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("role")
    override fun addRole(roleDto: RoleDto): List<RoleDto> {
        return adminService.addRole(roleDto)
    }

    override fun changeUserRole() {
        TODO("Not yet implemented")
    }

    override fun blockUser() {
        TODO("Not yet implemented")
    }

    override fun unblockUser() {
        TODO("Not yet implemented")
    }

}