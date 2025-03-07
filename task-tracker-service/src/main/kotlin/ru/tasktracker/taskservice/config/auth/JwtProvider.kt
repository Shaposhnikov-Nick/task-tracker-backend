package ru.tasktracker.taskservice.config.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.stereotype.Service
import ru.tasktracker.taskservice.exception.AuthenticationException
import java.security.Key
import javax.crypto.SecretKey


interface JwtProvider {
    fun validateAccessToken(accessToken: String): Boolean
    fun getAccessClaims(token: String): Claims
}


/**
 * Class for validate token
 */
@Service
class JwtProviderImpl(
    private val jwtProperty: JwtProperty
) : JwtProvider {

    private val jwtAccessSecret: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperty.accessSecret))

    override fun validateAccessToken(accessToken: String): Boolean {
        return validateToken(accessToken, jwtAccessSecret)
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

}