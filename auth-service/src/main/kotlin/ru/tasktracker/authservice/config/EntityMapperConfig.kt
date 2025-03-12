package ru.tasktracker.authservice.config

import cz.encircled.skom.MappingConfig
import cz.encircled.skom.SimpleKotlinObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.authority.SimpleGrantedAuthority
import ru.tasktracker.authservice.auth.JwtUserDetails
import ru.tasktracker.authservice.entity.User


@Configuration
class EntityMapperConfig {

    @PostConstruct
    fun init() {
        cz.encircled.skom.Extensions.mapper = SimpleKotlinObjectMapper {
            userToJwtUserDetails()
        }
    }

    private fun MappingConfig.userToJwtUserDetails() {
        forClasses(User::class, JwtUserDetails::class) {
            addPropertyMappings {
                mapOf(
                    "pass" to it.password,
                    "roles" to it.roles.map { roleEntity -> SimpleGrantedAuthority(roleEntity.name) }.toMutableSet()
                )
            }
        }
    }

}
