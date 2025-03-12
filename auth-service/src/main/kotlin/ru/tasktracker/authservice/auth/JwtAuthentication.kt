package ru.tasktracker.authservice.auth

import cz.encircled.skom.Convertable
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


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

data class JwtUserDetails(
    val id: Long,
    val login: String,
    val pass: String,
    var emailConfirmed: Boolean,
    val roles: MutableSet<SimpleGrantedAuthority>
) : UserDetails, Convertable {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles
    override fun getPassword(): String = pass
    override fun getUsername(): String = login
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = false
}

data class AuthenticatedUser(
    val id: Long,
    val login: String,
    val emailConfirmed: Boolean
)