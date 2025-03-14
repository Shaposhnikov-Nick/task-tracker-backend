package ru.tasktracker.authservice.auth

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.tasktracker.authservice.exception.AuthenticationException
import ru.tasktracker.authservice.exception.ExceptionResponse
import ru.tasktracker.authservice.extentions.getString


/**
 * Filter for validate jwt access token
 */
@Component
class JwtFilter(
    val jwtProvider: JwtProvider,
    val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    val notSecuredEndpoints = listOf("/v1/auth/login", "/v1/auth/token", "/v1/auth/logout", "/v1/auth/refresh")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val bearerToken: String = request.getHeader(HttpHeaders.AUTHORIZATION)
                ?: throw AuthenticationException("Need to send token in header Authorization")
            val token = bearerToken.substring(7)
            if (jwtProvider.validateAccessToken(token)) {
                val claims = jwtProvider.getAccessClaims(token)
                SecurityContextHolder.getContext().authentication = generateAuthentication(claims)
                filterChain.doFilter(request, response)
            }
        } catch (e: AuthenticationException) {
            val errorResponse = ExceptionResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.name,
                e.message,
            )
            response.contentType = "application/json"
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        }
    }

    private fun generateAuthentication(claims: Claims): JwtAuthentication {
        val authentication = JwtAuthentication(true)
        authentication.user = userFromClaims(claims)
        authentication.roles = getRoles(claims)
        return authentication
    }

    private fun userFromClaims(claims: Claims): AuthenticatedUser {
        return AuthenticatedUser(
            claims.getString("sub").toLong(),
            claims.getString("login"),
            claims.getString("emailConfirmed") == "true"
        )
    }

    fun getRoles(claims: Claims): MutableSet<SimpleGrantedAuthority> {
        val rolesList = claims.get("roles", ArrayList::class.java)
        return rolesList.map { SimpleGrantedAuthority("ROLE_${it.toString().uppercase()}") }.toMutableSet()
    }

    /**
     * Endpoints not covered by the jwt filter
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.servletPath in notSecuredEndpoints
}