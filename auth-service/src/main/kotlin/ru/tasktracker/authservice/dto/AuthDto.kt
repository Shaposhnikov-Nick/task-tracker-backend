package ru.tasktracker.authservice.dto

import jakarta.validation.constraints.NotBlank


data class UserAuthRequest(
    @field:NotBlank(message = "Login must not be empty")
    val login: String,
    @field:NotBlank(message = "Password must not be empty")
    val password: String,
) {
    override fun toString(): String {
        return "UserAuthRequest(login='$login')"
    }
}

data class RefreshJwtRequest(
    @field:NotBlank(message = "Refresh token must not be empty")
    val refreshToken: String
)

data class JwtUserTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val type: String = "Bearer",
    val user: JwtUserDetailsDto? = null
)

data class JwtUserDetailsDto(
    val id: Long,
    val login: String,
    val emailConfirmed: Boolean,
    val roles: List<String>
)