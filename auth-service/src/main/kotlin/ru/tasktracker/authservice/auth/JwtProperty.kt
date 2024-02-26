package ru.tasktracker.authservice.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration for generating access and refresh tokens.
 * accessSecret and refreshSecret - secret words in base64 format for signing and validating tokens
 * accessExpire and refreshExpire - token validity period (hours)
 */

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperty {
    var accessSecret: String = ""
    var accessExpire: Long = 0
    var refreshSecret: String = ""
    var refreshExpire: Long = 0
}