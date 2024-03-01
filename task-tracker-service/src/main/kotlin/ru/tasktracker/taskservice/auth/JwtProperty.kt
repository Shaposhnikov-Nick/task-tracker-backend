package ru.tasktracker.taskservice.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration for generating access and refresh tokens.
 * accessSecret - secret words in base64 format for signing and validating tokens
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperty {
    var accessSecret: String = ""
}