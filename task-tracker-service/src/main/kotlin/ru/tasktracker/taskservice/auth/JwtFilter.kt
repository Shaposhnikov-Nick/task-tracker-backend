package ru.tasktracker.taskservice.auth

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.tasktracker.taskservice.exception.AuthenticationException
import ru.tasktracker.taskservice.exception.ExceptionResponse
import ru.tasktracker.taskservice.extentions.getString


/**
 * Filter for validate jwt access token
 */
@Component
class JwtFilter(
    val jwtProvider: JwtProvider,
    val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    val authorizationHeader = "Authorization"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val bearerToken: String = request.getHeader(authorizationHeader)
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

    private fun getRoles(claims: Claims): MutableSet<SimpleGrantedAuthority> {
        val rolesList = claims.get("roles", ArrayList::class.java)
        return rolesList.map { SimpleGrantedAuthority("ROLE_${it.toString().uppercase()}") }.toMutableSet()
    }

    /**
     * Endpoints not covered by the jwt filter
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean = false
}