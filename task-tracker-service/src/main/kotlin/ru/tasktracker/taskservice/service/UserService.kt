package ru.tasktracker.taskservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.taskservice.dto.UserDto
import ru.tasktracker.taskservice.entity.User
import ru.tasktracker.taskservice.repository.UserRepository

interface UserService {

    fun createUser(userDto: UserDto): UserDto

    fun getUser(id: Long): UserDto
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository
) : UserService {

    @Transactional
    override fun createUser(userDto: UserDto): UserDto {
        val user = userDto.mapTo<User>()
        user.addRole(userDto.roles!!.first().mapTo())
        return userRepository.saveAndFlush(user).mapTo()
    }

    @Transactional(readOnly = true)
    override fun getUser(id: Long): UserDto {
        return userRepository.findUserById(id)?.mapTo() ?: throw RuntimeException("User with id $id not found")
    }

}