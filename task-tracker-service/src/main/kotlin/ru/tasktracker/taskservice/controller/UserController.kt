package ru.tasktracker.taskservice.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.taskservice.dto.UserDto
import ru.tasktracker.taskservice.dto.validation.ValidationGroups
import ru.tasktracker.taskservice.service.UserService


@Validated
interface UserController {

    fun createUser(@RequestBody @Validated(ValidationGroups.Create::class) userDto: UserDto): UserDto

    fun updateUser(@RequestBody @Validated(ValidationGroups.Update::class) userDto: UserDto): UserDto

    fun getUser(@PathVariable id: Long): UserDto

}

@RestController
@RequestMapping("user")
class UserControllerImpl(
    val userService: UserService
) : UserController {

    @PostMapping
    override fun createUser(userDto: UserDto): UserDto {
        return userService.createUser(userDto)
    }

    @PutMapping
    override fun updateUser(userDto: UserDto): UserDto {
        return userService.updateUserProfile(userDto)
    }

    @GetMapping("/{id}")
    override fun getUser(id: Long): UserDto {
        return userService.getUser(id)
    }

}