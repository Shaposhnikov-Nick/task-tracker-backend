package ru.tasktracker.adminservice.service

import cz.encircled.skom.Extensions.mapTo
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.adminservice.dto.ChangeUserRoleAction
import ru.tasktracker.adminservice.dto.ChangeUserRolesDto
import ru.tasktracker.adminservice.dto.RoleDto
import ru.tasktracker.adminservice.entity.Role
import ru.tasktracker.adminservice.exception.RoleException
import ru.tasktracker.adminservice.exception.UserException
import ru.tasktracker.adminservice.repository.RoleRepository
import ru.tasktracker.adminservice.repository.UserRepository


interface AdminService {
    fun addRole(roleDto: RoleDto): List<RoleDto>
    fun changeUserRole(changedRoles: ChangeUserRolesDto, action: ChangeUserRoleAction): List<RoleDto>
    fun blockUser(userId: Long, block: Boolean)

}

@Service
class AdminServiceImpl(
    val roleRepository: RoleRepository,
    val userRepository: UserRepository
) : AdminService {

    val log = LoggerFactory.getLogger(this::class.java)

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

    @Transactional
    override fun blockUser(userId: Long, block: Boolean) {
        val user = userRepository.findUserById(userId)
            ?: throw UserException("User with id $userId not found")

        user.blocked = block

        userRepository.saveAndFlush(user)
    }

    /**
     * TODO: fix roles later
     */
    @EventListener(ApplicationReadyEvent::class)
    fun initRoles() {
        log.info("Init admin and user roles")
        roleRepository.saveAll(listOf(Role("admin"), Role("user")))
    }

}