package ru.tasktracker.adminservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.adminservice.dto.ChangeUserRoleAction
import ru.tasktracker.adminservice.dto.ChangeUserRolesDto
import ru.tasktracker.adminservice.dto.RoleDto
import ru.tasktracker.adminservice.exception.RoleException
import ru.tasktracker.adminservice.exception.UserException
import ru.tasktracker.adminservice.repository.RoleRepository
import ru.tasktracker.adminservice.repository.UserRepository


interface AdminService {
    fun addRole(roleDto: RoleDto): List<RoleDto>
    fun changeUserRole(changedRoles: ChangeUserRolesDto, action: ChangeUserRoleAction): List<RoleDto>

}

@Service
class AdminServiceImpl(
    val roleRepository: RoleRepository,
    val userRepository: UserRepository
) : AdminService {

    @Transactional
    override fun addRole(roleDto: RoleDto): List<RoleDto> {
        if (roleRepository.existsRoleByName(roleDto.name))
            throw RoleException("Role ${roleDto.name} already exists")

        roleRepository.save(roleDto.mapTo())
        return roleRepository.findAll().mapTo()
    }

    @Transactional
    override fun changeUserRole(changedRoles: ChangeUserRolesDto, action: ChangeUserRoleAction): List<RoleDto> {
        val user = userRepository.findUserById(changedRoles.userId)
            ?: throw UserException("User with id ${changedRoles.userId} not found")

        changedRoles.roles.forEach {
            if (action == ChangeUserRoleAction.ADD)
                user.addRole(it.mapTo())

            if (action == ChangeUserRoleAction.REMOVE)
                user.removeRole(it.mapTo())
        }

        userRepository.saveAndFlush(user)
        return roleRepository.findRolesByUserId(changedRoles.userId).mapTo()
    }

}