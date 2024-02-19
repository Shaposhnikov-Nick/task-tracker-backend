package ru.tasktracker.taskservice.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import cz.encircled.skom.Convertable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import ru.tasktracker.taskservice.entity.Role
import ru.tasktracker.taskservice.entity.Task
import java.time.LocalDateTime


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class JwtUser(
    val login: String,
    val password: String,
    val email: String,
    val emailConfirmed: Boolean,
    val about: String,
    val blocked: Boolean,
    val role: Role,
    val profile: UserProfileDto,
    val tasks: MutableSet<Task> = mutableSetOf(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val id: Long? = null,

    @field:NotBlank(message = "Login can't be empty")
    val login: String? = null,

    @field:NotBlank(message = "Password can't be empty")
    val password: String? = null,

    @field:Email(message = "The string has to be a well-formed email address")
    val email: String,
    val emailConfirmed: Boolean? = null,
    val about: String?,
    val blocked: Boolean? = null,
    val roles: List<RoleDto>? = null,
    val profile: UserProfileDto,
    val tasks: MutableSet<Task> = mutableSetOf(),
    var createdDate: LocalDateTime? = null,
    var updateDate: LocalDateTime? = null,
    var updatedBy: String? = null
) : Convertable

data class RoleDto(
    val id: Long,
    val role: String
) : Convertable


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserProfileDto(

    @field:NotBlank(message = "Name can't be empty")
    val name: String,

    @field:NotBlank(message = "Lastname can't be empty")
    val lastName: String,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val birthday: LocalDateTime,

    val avatar: ByteArray? = null
) : Convertable