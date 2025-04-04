package ru.tasktracker.taskservice.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import cz.encircled.skom.Convertable
import jakarta.validation.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import ru.tasktracker.taskservice.dto.validation.ValidationGroups
import java.time.LocalDateTime


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(

    @field:Null(groups = [ValidationGroups.Create::class], message = "Id must be null")
    var id: Long? = null,

    @field:NotBlank(groups = [ValidationGroups.Create::class], message = "Login can't be empty")
    @field:Size(groups = [ValidationGroups.Create::class], min = 5, max = 50, message = "Login must be between 5 and 50 characters")
    val login: String? = null,

    @field:NotBlank(groups = [ValidationGroups.Create::class], message = "Password can't be empty")
    @field:Size(groups = [ValidationGroups.Create::class],min = 7, max = 50, message = "Password must be between 7 and 50 characters")
    var password: String? = null,
    val emailConfirmed: Boolean? = null,
    val blocked: Boolean? = null,
    val roles: List<RoleDto>? = null,
    val profile: UserProfileDto,
    var createdDate: LocalDateTime? = null,
    var updateDate: LocalDateTime? = null,
    var updatedBy: String? = null
) : Convertable


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserProfileDto(

    @field:NotBlank(groups = [ValidationGroups.Create::class, ValidationGroups.Update::class], message = "Name can't be empty")
    val name: String,

    @field:NotBlank(groups = [ValidationGroups.Create::class, ValidationGroups.Update::class], message = "Lastname can't be empty")
    val lastName: String,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val birthday: LocalDateTime,

    @field:Email(
        groups = [ValidationGroups.Create::class],
        message = "The string has to be a well-formed email address"
    )
    val email: String? = null,

    val about: String? = null,

    val avatar: ByteArray? = null
) : Convertable

data class PaginationParams(

    @field:Min(1)
    val pageNumber: Int = 1,

    @field:Min(1)
    val pageSize: Int = 1
)

data class UsersPageResponse(
    val content: List<UserDto>,
    val page: Int,
    val totalPages: Int,
    val totalItems: Long
)

data class ChangeUserRolesDto(

    @field:NotNull(message = "User id must not be null")
    val userId: Long,

    @field:NotEmpty(message = "List of roles must not be empty")
    val roles: Set<RoleDto>
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RoleDto(
    val id: Long? = null,

    @field:NotEmpty
    val name: String

): Convertable

enum class ChangeUserRoleAction {
    ADD,
    REMOVE
}