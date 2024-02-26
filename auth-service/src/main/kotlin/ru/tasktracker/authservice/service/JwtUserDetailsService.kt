package ru.tasktracker.authservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.authservice.auth.JwtUserDetails
import ru.tasktracker.authservice.exception.AuthenticationException
import ru.tasktracker.authservice.repository.UserRepository


@Service
class JwtUserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): JwtUserDetails {
        val user = userRepository.findUserByLoginAndBlockedIsFalse(username)
            ?: throw AuthenticationException("Неверный логин или пароль")
        return user.mapTo()
    }
}