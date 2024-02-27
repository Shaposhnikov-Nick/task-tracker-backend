package ru.tasktracker.taskservice.config

import cz.encircled.skom.MappingConfig
import cz.encircled.skom.SimpleKotlinObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import ru.tasktracker.taskservice.dto.TaskDto
import ru.tasktracker.taskservice.entity.Task


@Configuration
class EntityMapperConfig {

    @PostConstruct
    fun init() {
        cz.encircled.skom.Extensions.mapper = SimpleKotlinObjectMapper {
            taskToTaskDto()
        }
    }



    private fun MappingConfig.taskToTaskDto() {
        forClasses(Task::class, TaskDto::class) {
            addPropertyMappings {
                mapOf(
                    "userId" to it.user?.id
                )
            }
        }
    }
}
