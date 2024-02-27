package ru.tasktracker.authservice.config

import cz.encircled.skom.MappingConfig
import cz.encircled.skom.SimpleKotlinObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.authority.SimpleGrantedAuthority
import ru.tasktracker.authservice.auth.JwtUserDetails
import ru.tasktracker.authservice.dto.UserDto
import ru.tasktracker.authservice.dto.UserProfileDto
import ru.tasktracker.authservice.entity.User
import ru.tasktracker.authservice.entity.UserProfile


@Configuration
class EntityMapperConfig {

    @PostConstruct
    fun init() {
        cz.encircled.skom.Extensions.mapper = SimpleKotlinObjectMapper {
            userToJwtUserDetails()
            userToUserDto()
            userDtoToUser()
            userProfileDtoToUserProfile()
        }
    }

    private fun MappingConfig.userToJwtUserDetails() {
        forClasses(User::class, JwtUserDetails::class) {
            addPropertyMappings {
                mapOf(
                    "pass" to it.password,
                    "roles" to it.roles.map { roleEntity -> SimpleGrantedAuthority(roleEntity.role) }.toMutableSet()
                )
            }
        }
    }

    private fun MappingConfig.userToUserDto() {
        forClasses(User::class, UserDto::class) {
            addPropertyMappings {
                mapOf(
                    "password" to null
                )
            }
        }
    }

    private fun MappingConfig.userDtoToUser() {
        forClasses(UserDto::class, User::class) {
            addPropertyMappings {
                mapOf(
                    "emailConfirmed" to false,
                    "blocked" to false,
                )
            }
        }
    }

    private fun MappingConfig.userProfileDtoToUserProfile() {
        forClasses(UserProfileDto::class, UserProfile::class) {
            addPropertyMappings {
                mapOf(
                    "avatar" to null
                )
            }
        }
    }

}
