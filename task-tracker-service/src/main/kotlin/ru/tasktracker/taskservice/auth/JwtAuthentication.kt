package ru.tasktracker.taskservice.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority


class JwtAuthentication(
    private var authenticated: Boolean = false
) : Authentication {
    var roles: MutableSet<SimpleGrantedAuthority> = mutableSetOf()

    lateinit var user: AuthenticatedUser

    override fun getName(): String = user.login

    override fun getAuthorities(): MutableSet<out GrantedAuthority> = roles

    override fun getCredentials(): Any = Any()

    override fun getDetails(): Any = Any()

    override fun getPrincipal(): Any = user

    override fun isAuthenticated(): Boolean = authenticated

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}

data class AuthenticatedUser(
    val id: Long,
    val login: String,
    val emailConfirmed: Boolean
)