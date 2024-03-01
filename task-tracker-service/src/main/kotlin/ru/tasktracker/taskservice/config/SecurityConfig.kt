package ru.tasktracker.taskservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.tasktracker.taskservice.auth.JwtFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val jwtFilter: JwtFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { it.disable() }
            .logout { it.disable() }
            .cors { }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}