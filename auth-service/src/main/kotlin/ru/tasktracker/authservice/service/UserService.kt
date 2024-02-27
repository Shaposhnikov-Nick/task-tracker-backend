package ru.tasktracker.authservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.authservice.dto.UserDto
import ru.tasktracker.authservice.entity.User
import ru.tasktracker.authservice.repository.UserRepository


interface UserService {

    fun createUser(userDto: UserDto): UserDto

    fun getUser(id: Long): UserDto

    fun updateUserProfile(userDto: UserDto): UserDto

    fun changePassword(userDto: UserDto): UserDto // TODO
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository
) : UserService {

    @Transactional
    override fun createUser(userDto: UserDto): UserDto {
        val user = userDto.mapTo<User>()
        user.addRole(userDto.roles!!.first().mapTo())
        return userRepository.saveAndFlush(user).mapTo<UserDto>()
    }

    @Transactional(readOnly = true)
    override fun getUser(id: Long): UserDto {
        return userRepository.findUserById(id)?.mapTo() ?: throw RuntimeException("User with id $id not found")
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