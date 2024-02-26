package ru.tasktracker.authservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.authservice.entity.RefreshToken
import ru.tasktracker.authservice.repository.RefreshTokenRepository

interface RefreshTokenStorageService {

    fun saveToken(login: String, token: String): RefreshToken

    fun getToken(login: String): String?
}


@Service
class RefreshTokenStorageServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository
) : RefreshTokenStorageService {

    @Transactional
    override fun saveToken(login: String, token: String): RefreshToken =
        refreshTokenRepository.save(RefreshToken(login, token))

    @Transactional(readOnly = true)
    override fun getToken(login: String) = refreshTokenRepository.findRefreshTokenByLogin(login)?.refreshToken
}