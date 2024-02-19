package ru.tasktracker.taskservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
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

            return if (auth == null || !auth.isAuthenticated)
                throw IllegalStateException("No user in SecurityContext!")
            else {
                val principal = auth.principal
                if (principal is org.springframework.security.core.userdetails.User) {
                    Optional.of(principal.username)
                } else {
                    Optional.of("none") // TODO: fix with real user
                }

            }
        }
    }

}