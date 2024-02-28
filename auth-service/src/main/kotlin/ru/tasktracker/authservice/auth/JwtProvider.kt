package ru.tasktracker.authservice.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.stereotype.Service
import ru.tasktracker.authservice.exception.AuthenticationException
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey


interface JwtProvider {

    fun generateAccessToken(user: JwtUserDetails): String

    fun generateRefreshToken(user: JwtUserDetails): String

    fun validateAccessToken(accessToken: String): Boolean

    fun validateRefreshToken(refreshToken: String): Boolean

    fun getAccessClaims(token: String): Claims

    fun getRefreshClaims(token: String): Claims
}


/***
 * Class for generate and validate tokens
 */
@Service
class JwtProviderImpl(
    private val jwtProperty: JwtProperty
) : JwtProvider {

    private val jwtAccessSecret: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperty.accessSecret))

    private val jwtRefreshSecret: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperty.refreshSecret))

    override fun generateAccessToken(user: JwtUserDetails): String {
        return Jwts.builder()
            .setSubject(user.id.toString())
            .claim("login", user.login)
            .claim("roles", user.roles.map { it.authority })
            .claim("emailConfirmed", user.emailConfirmed.toString())
            .setExpiration(accessExpiration(jwtProperty.accessExpire))
            .signWith(jwtAccessSecret)
            .compact()
    }

    override fun generateRefreshToken(user: JwtUserDetails): String {
        return Jwts.builder()
            .setSubject(user.login)
            .setExpiration(accessExpiration(jwtProperty.refreshExpire))
            .signWith(jwtRefreshSecret)
            .compact()
    }

    override fun validateAccessToken(accessToken: String): Boolean {
        return validateToken(accessToken, jwtAccessSecret)
    }

    override fun validateRefreshToken(refreshToken: String): Boolean {
        return validateToken(refreshToken, jwtRefreshSecret)
    }

    private fun validateToken(token: String, secret: Key): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: ExpiredJwtException) {
            throw AuthenticationException("Token expired")
        } catch (e: UnsupportedJwtException) {
            throw AuthenticationException("Unsupported jwt")
        } catch (e: MalformedJwtException) {
            throw AuthenticationException("Malformed jwt")
        } catch (e: SecurityException) {
            throw AuthenticationException("Invalid signature")
        } catch (e: Exception) {
            throw AuthenticationException("Invalid token")
        }
    }

    override fun getAccessClaims(token: String): Claims {
        return getClaims(token, jwtAccessSecret)
    }

    override fun getRefreshClaims(token: String): Claims {
        return getClaims(token, jwtRefreshSecret)
    }

    private fun getClaims(token: String, secret: Key): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: JwtException) {
            throw AuthenticationException(e.message ?: "Error when getting claims from token")
        }
    }

    private fun accessExpiration(expiresOn: Long) =
        Date.from(LocalDateTime.now().plusHours(expiresOn).atZone(ZoneId.systemDefault()).toInstant())
}