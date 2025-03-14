package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.taskservice.dto.*
import ru.tasktracker.taskservice.entity.Role
import ru.tasktracker.taskservice.entity.User
import ru.tasktracker.taskservice.exception.RoleException
import ru.tasktracker.taskservice.exception.UserException
import ru.tasktracker.taskservice.repository.RoleRepository
import ru.tasktracker.taskservice.repository.UserRepository


interface UserService {
    fun registerUser(userDto: UserDto): UserDto
    fun getUser(id: Long): UserDto
    fun getAllUsers(paginationParams: PaginationParams): UsersPageResponse
    fun updateUserProfile(userDto: UserDto): UserDto
    fun changePassword(userDto: UserDto): UserDto // TODO
    fun addRole(roleDto: RoleDto): List<RoleDto>
    fun changeUserRole(changedRoles: ChangeUserRolesDto, action: ChangeUserRoleAction): List<RoleDto>
    fun blockUser(userId: Long, block: Boolean)
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder
) : UserService {

    val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun registerUser(userDto: UserDto): UserDto {
        userDto.password = passwordEncoder.encode(userDto.password)
        val user = userDto.mapTo<User>()
        userDto.roles!!.forEach {
            user.addRole(it.mapTo())
        }
        return userRepository.saveAndFlush(user).mapTo<UserDto>()
    }

    @Transactional(readOnly = true)
    override fun getUser(id: Long): UserDto {
        return userRepository.findUserById(id)?.mapTo() ?: throw RuntimeException("User with id $id not found")
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(paginationParams: PaginationParams): UsersPageResponse {
        val page = PageRequest.of(paginationParams.pageNumber - 1, paginationParams.pageSize, Sort.by("id"))
        val usersPage = userRepository.findAll(page)
        return UsersPageResponse(
            usersPage.content.mapTo(),
            usersPage.number + 1,
            usersPage.totalPages,
            usersPage.totalElements
        )
    }

    @Transactional
    override fun updateUserProfile(userDto: UserDto): UserDto {
        val user = userRepository.findUserById(userDto.id!!)
            ?: throw RuntimeException("User with id ${userDto.id} not found")

        user.profile.about = userDto.profile.about
        user.profile.name = userDto.profile.name
        user.profile.lastName = userDto.profile.lastName
        user.profile.birthday = userDto.profile.birthday
        userDto.profile.email?.let {
            user.profile.email = it
            user.emailConfirmed = false
        }

        return userRepository.saveAndFlush(user).mapTo()
    }

    override fun changePassword(userDto: UserDto): UserDto {
        TODO("Not yet implemented")
    }

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
        if (!roleRepository.existsRoleByName("admin") && !roleRepository.existsRoleByName("user")) {
            log.info("Init admin and user roles")
            roleRepository.saveAll(listOf(Role("admin"), Role("user")))
        }
    }

}