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
    val login: String,

    @Column
    val password: String,

    @Column
    val email: String,

    @Column(name = "email_confirmed")
    val emailConfirmed: Boolean,

    @Column
    val about: String,

    @Column
    val blocked: Boolean,

    @Enumerated(EnumType.STRING)
    val role: Role,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "profile_id")
    val profile: UserProfile,

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val tasks: MutableSet<Task> = mutableSetOf(),
) : BaseEntity()


@Entity
@Table(name = "UsersProfile")
class UserProfile(

    @OneToOne(mappedBy = "profile")
    val user: User,

    @Column
    val name: String,

    @Column(name = "last_name")
    val lastName: String,

    @Column
    val birthday: LocalDate,

    @Column
    val avatar: ByteArray
) : BaseEntity()


@Entity
@Table(name = "Task")
class Task(

    @Column
    val title: String,

    @Column
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    val group: TaskGroup?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column
    val parentId: Long?,

    @Enumerated(EnumType.STRING)
    val status: TaskStatus,

    @Enumerated(EnumType.STRING)
    val priority: TaskPriority,

    @Column
    val deadLine: LocalDateTime,

    @Column
    val reporterId: Long,

    @Column
    val assigneeId: Long,

    @ManyToMany
    val tags: Set<Tag> = mutableSetOf(), // TODO

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val comments: Set<Comment> = mutableSetOf()
) : BaseEntity()


@Entity
@Table(name = "Comment")
class Comment(

    @Column
    val description: String,

    @Column
    val userId: Long,

//    val images: List<ByteArray>, // TODO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    val task: Task
) : BaseEntity()


@Entity
@Table(name = "Tag")
class Tag(
    val name: String,
) : BaseEntity()


@Entity
@Table(name = "TaskGroup")
class TaskGroup(

    @Column
    val title: String,

    @Column
    val description: String,

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val tasks: Set<Task> = mutableSetOf(),
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