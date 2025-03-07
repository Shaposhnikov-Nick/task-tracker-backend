package ru.tasktracker.taskservice.config

import cz.encircled.skom.MappingConfig
import cz.encircled.skom.SimpleKotlinObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.dto.UserDto
import ru.tasktracker.taskservice.dto.UserProfileDto
import ru.tasktracker.taskservice.entity.Task
import ru.tasktracker.taskservice.entity.User
import ru.tasktracker.taskservice.entity.UserProfile


@Configuration
class EntityMapperConfig {

    @PostConstruct
    fun init() {
        cz.encircled.skom.Extensions.mapper = SimpleKotlinObjectMapper {
            taskToTaskDto()
            userToUserDto()
            userDtoToUser()
            userProfileDtoToUserProfile()
        }
    }

    private fun MappingConfig.taskToTaskDto() {
        forClasses(Task::class, TaskDto::class) {
            addPropertyMappings {
                mapOf(
                    "userId" to it.user?.id,
                    "groupId" to it.group?.id
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
