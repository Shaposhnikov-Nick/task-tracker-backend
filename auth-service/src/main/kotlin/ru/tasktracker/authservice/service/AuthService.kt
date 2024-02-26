package ru.tasktracker.authservice.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.tasktracker.authservice.auth.JwtProvider
import ru.tasktracker.authservice.dto.RefreshJwtRequest
import ru.tasktracker.authservice.dto.UserAuthRequest
import ru.tasktracker.authservice.dto.JwtUserTokenResponse
import ru.tasktracker.authservice.exception.AuthenticationException
import ru.tasktracker.authservice.extentions.toDto


interface AuthService {

    fun login(userAuthRequest: UserAuthRequest): JwtUserTokenResponse

    fun logout(userAuthRequest: UserAuthRequest)

    fun getNewAccessToken(refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse

    fun getNewRefreshToken(refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse
}


@Service
class AuthServiceImpl(
    val refreshTokenStorageService: RefreshTokenStorageService,
    val userDetailsService: JwtUserDetailsServiceImpl,
    val passwordEncoder: PasswordEncoder,
    val jwtProvider: JwtProvider
) : AuthService {

    override fun login(userAuthRequest: UserAuthRequest): JwtUserTokenResponse {
        val userDetails = userDetailsService.loadUserByUsername(userAuthRequest.login)
        if (passwordEncoder.matches(userAuthRequest.password, userDetails.password)) {
            val accessToken = jwtProvider.generateAccessToken(userDetails)
            val refreshToken = jwtProvider.generateRefreshToken(userDetails)
            refreshTokenStorageService.saveToken(userDetails.login, refreshToken)
            return JwtUserTokenResponse(accessToken, refreshToken, user = userDetails.toDto())
        }
        throw AuthenticationException("Wrong login or password")
    }

    override fun logout(userAuthRequest: UserAuthRequest) {
        TODO("Not yet implemented")
    }

    override fun getNewAccessToken(refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse {
        TODO("Not yet implemented")
    }

    override fun getNewRefreshToken(refreshJwtRequest: RefreshJwtRequest): JwtUserTokenResponse {
        TODO("Not yet implemented")
    }

}
