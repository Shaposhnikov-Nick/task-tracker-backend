package ru.tasktracker.adminservice.config

import cz.encircled.skom.SimpleKotlinObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration


@Configuration
class EntityMapperConfig {

    @PostConstruct
    fun init() {
        cz.encircled.skom.Extensions.mapper = SimpleKotlinObjectMapper {
        }
    }

}
