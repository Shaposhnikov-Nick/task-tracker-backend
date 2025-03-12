package ru.tasktracker.authservice.entity

import cz.encircled.skom.Convertable
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @CreatedDate
    @Column(name = "created_date")
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_date")
    var updateDate: LocalDateTime? = null,

    @LastModifiedBy
    @Column(name = "updated_by", updatable = false)
    var updatedBy: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BaseEntity
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}


@Entity
@Table(name = "`Users`", schema = "task_tracker")
class User(

    @Column
    val login: String,

    @Column
    val password: String,

    @Column(name = "email_confirmed")
    var emailConfirmed: Boolean,

    @Column
    val blocked: Boolean,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "`UserRole`", schema = "task_tracker",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),

) : BaseEntity(), Convertable

@Entity
@Table(name = "`Role`", schema = "task_tracker")
class Role(

    @Column
    val name: String,

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<User> = mutableSetOf()

) : BaseEntity(), Convertable


@Entity
@Table(name = "`RefreshToken`", schema = "auth_service")
class RefreshToken(

    @Id
    @Column(name = "login")
    val login: String,

    @Column(name = "refresh_token")
    val refreshToken: String

)