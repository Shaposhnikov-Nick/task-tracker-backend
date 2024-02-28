package ru.tasktracker.authservice.extentions

import io.jsonwebtoken.Claims
import ru.tasktracker.authservice.auth.JwtUserDetails
import ru.tasktracker.authservice.dto.JwtUserDetailsDto


fun JwtUserDetails.toDto(): JwtUserDetailsDto {
    val rolesDto = this.roles.map { it.authority }
    return JwtUserDetailsDto(this.id, this.login, this.emailConfirmed, rolesDto)
}

fun Claims.getString(key: String): String {
    return get(key, String::class.java) ?: ""
}