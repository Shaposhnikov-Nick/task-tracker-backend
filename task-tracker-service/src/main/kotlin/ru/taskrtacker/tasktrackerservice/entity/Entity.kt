package ru.taskrtacker.tasktrackerservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column(name = "created_date")
    lateinit var createdDate: LocalDateTime

    @LastModifiedDate
    @Column(name = "update_date")
    lateinit var updateDate: LocalDateTime

    @LastModifiedBy
    @Column(name = "updated_by", updatable = false)
    lateinit var updatedBy: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BaseEntity
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode()
    }
}

@Entity
@Table(name = "Users")
class User(
    @Column
    val name: String,
    @Column(name = "last_name")
    val lastName: String,
    @Column
    val birthday: LocalDate,
    @Column
    val email: String,
    @Column(name = "email_confirmed")
    val emailConfirmed: Boolean,
    @Column
    val about: String,
    @Column
    val blocked: Boolean,
    val role: Role,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tasks: MutableSet<Task> = mutableSetOf(),
    val taskGroups: Set<TaskGroup> = mutableSetOf()
) : BaseEntity()


@Entity
@Table(name = "Task")
class Task(
    val title: String,
    val description: String,
    @OneToOne
    val group: TaskGroup?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    val parentId: Long?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val deadLine: LocalDateTime,
    val reporter: User,
    val assignee: User,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val tags: Set<Tag> = mutableSetOf(),
    val comments: Set<Comment> = mutableSetOf()
) : BaseEntity()

@Entity
@Table(name = "Comment")
class Comment(
    val id: Long? = null,
    val description: String,
    val user: User,
    val images: List<ByteArray>,
    val created: LocalDateTime,
    val updated: LocalDateTime
) : BaseEntity()

@Entity
@Table(name = "Tag")
class Tag(
    val id: Long? = null,
    val name: String,
    val created: LocalDateTime,
    val updated: LocalDateTime
) : BaseEntity()


@Entity
@Table(name = "TaskGroup")
class TaskGroup(
    val title: String,
    val description: String,
    val tasks: Set<Task> = mutableSetOf(),
    val created: LocalDateTime,
    val updated: LocalDateTime
) : BaseEntity()


enum class Role {
    USER,
    ADMIN
}

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}

enum class TaskPriority {
    HIGH,
    MEDIUM,
    LOW
}