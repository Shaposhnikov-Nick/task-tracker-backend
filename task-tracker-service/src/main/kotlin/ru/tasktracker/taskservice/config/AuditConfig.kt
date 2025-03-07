package ru.tasktracker.taskservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import ru.tasktracker.taskservice.config.auth.AuthenticatedUser
import java.util.*

@Configuration
@EnableJpaAuditing
class AuditConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return SpringSecurityAuditorAware
    }

    companion object SpringSecurityAuditorAware : AuditorAware<String> {
        override fun getCurrentAuditor(): Optional<String> {
            val auth = SecurityContextHolder.getContext().authentication

            return if (auth != null && auth.isAuthenticated) {
                val principal = auth.principal
                if (principal is AuthenticatedUser) {
                    Optional.of(principal.login)
                } else {
                    Optional.of("none")
                }
            } else {
                Optional.of("none")
            }
        }
    }

}