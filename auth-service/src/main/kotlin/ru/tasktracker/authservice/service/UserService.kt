package ru.tasktracker.authservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.authservice.dto.PaginationParams
import ru.tasktracker.authservice.dto.UserDto
import ru.tasktracker.authservice.dto.UsersPageResponse
import ru.tasktracker.authservice.entity.User
import ru.tasktracker.authservice.repository.UserRepository


interface UserService {

    fun registerUser(userDto: UserDto): UserDto

    fun getUser(id: Long): UserDto

    fun getAllUsers(paginationParams: PaginationParams): UsersPageResponse

    fun updateUserProfile(userDto: UserDto): UserDto

    fun changePassword(userDto: UserDto): UserDto // TODO
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) : UserService {

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

}