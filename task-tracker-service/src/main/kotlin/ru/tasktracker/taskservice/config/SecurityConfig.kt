package ru.tasktracker.taskservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import ru.tasktracker.taskservice.config.auth.JwtFilter
import java.security.SecureRandom


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    val jwtFilter: JwtFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .formLogin { it.disable() }
            .logout { it.disable() }
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/*/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/v1/users/reg")).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(10, SecureRandom())

}