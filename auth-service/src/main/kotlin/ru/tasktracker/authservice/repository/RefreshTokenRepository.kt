package ru.tasktracker.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tasktracker.authservice.entity.RefreshToken

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {

    fun findRefreshTokenByLogin(login: String): RefreshToken?
}