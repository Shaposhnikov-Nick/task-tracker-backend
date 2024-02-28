package ru.tasktracker.authservice.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.authservice.auth.AuthenticatedUser
import ru.tasktracker.authservice.dto.UserDto
import ru.tasktracker.authservice.service.UserService
import ru.tasktracker.taskservice.dto.validation.ValidationGroups


@Validated
interface UserController {

    fun registerUser(@RequestBody @Validated(ValidationGroups.Create::class) userDto: UserDto): UserDto

    fun updateUser(
        @AuthenticationPrincipal authUser: AuthenticatedUser,
        @RequestBody @Validated(ValidationGroups.Update::class) userDto: UserDto
    ): UserDto

    fun getUser(@AuthenticationPrincipal authUser: AuthenticatedUser): UserDto

}

@RestController
@RequestMapping("users")
class UserControllerImpl(
    val userService: UserService
) : UserController {

    @PostMapping("reg")
    override fun registerUser(userDto: UserDto): UserDto {
        return userService.registerUser(userDto)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping
    override fun updateUser(authUser: AuthenticatedUser, userDto: UserDto): UserDto {
        userDto.id = authUser.id
        return userService.updateUserProfile(userDto)
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    override fun getUser(authUser: AuthenticatedUser): UserDto {
        return userService.getUser(authUser.id)
    }

}