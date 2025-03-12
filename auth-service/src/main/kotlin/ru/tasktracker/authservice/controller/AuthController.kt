package ru.tasktracker.authservice.controller

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tasktracker.authservice.dto.RefreshJwtRequest
import ru.tasktracker.authservice.dto.UserAuthRequest
import ru.tasktracker.authservice.dto.JwtUserTokenResponse
import ru.tasktracker.authservice.service.AuthService

@Validated
interface AuthController {
    fun login(@RequestBody @Valid userAuthRequest: UserAuthRequest): JwtUserTokenResponse
    fun logout(@RequestBody @Valid userAuthRequest: UserAuthRequest)
    fun getNewAccessToken(@RequestBody @Valid refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse
    fun getNewRefreshToken(@RequestBody @Valid refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse
}


@RestController
@RequestMapping("auth")
class AuthControllerImpl(
    val authService: AuthService
): AuthController {

    @PostMapping("login")
    override fun login(userAuthRequest: UserAuthRequest): JwtUserTokenResponse {
       return authService.login(userAuthRequest)
    }

    @PostMapping("logout")
    override fun logout(userAuthRequest: UserAuthRequest) {
        TODO("Not yet implemented")
    }

    @PostMapping("token")
    override fun getNewAccessToken(refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse {
        TODO("Not yet implemented")
    }

    @PostMapping("refresh")
    override fun getNewRefreshToken(refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse {
        TODO("Not yet implemented")
    }

}