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
    val about: String?,

    @Column
    val blocked: Boolean,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "UserRole",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "profile_id")
    val profile: UserProfile,

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val tasks: MutableSet<Task> = mutableSetOf()

) : BaseEntity() {

    fun addRole(role: Role) {
        roles.add(role)
        role.users.add(this)
    }

    fun removeRole(role: Role) {
        roles.remove(role)
        role.users.remove(this)
    }

    fun addTask(task: Task) {
        tasks.add(task)
        task.user = this
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
        task.user = null
    }
}


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

    @Lob
    @Column
    val avatar: ByteArray?

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
    var group: TaskGroup?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "parent_id")
    val parentId: Long?,

    @Enumerated(EnumType.STRING)
    val status: TaskStatus,

    @Enumerated(EnumType.STRING)
    val priority: TaskPriority,

    @Column
    val deadLine: LocalDateTime,

    @Column(name = "assignee_id")
    val assigneeId: Long,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "TaskTag",
        joinColumns = [JoinColumn(name = "task_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")]
    )
    val tags: MutableSet<Tag> = mutableSetOf(),

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val taskComments: MutableSet<TaskComment> = mutableSetOf()

) : BaseEntity() {

    fun addTag(tag: Tag) {
        tags.add(tag)
        tag.tasks.add(this)
    }

    fun removeTag(tag: Tag) {
        tags.remove(tag)
        tag.tasks.remove(this)
    }

    fun addComment(comment: TaskComment) {
        taskComments.add(comment)
        comment.task = this
    }

    fun removeComment(comment: TaskComment) {
        taskComments.remove(comment)
        comment.task = null
    }

}


@Entity
@Table(name = "TaskComment")
class TaskComment(

    @Column
    val description: String,

    @Column
    val userId: Long,

    @OneToMany(mappedBy = "comment", fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    val images: MutableSet<Image> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    var task: Task?

) : BaseEntity() {

    fun addImage(image: Image) {
        images.add(image)
        image.comment = this
    }

    fun removeImage(image: Image) {
        images.remove(image)
        image.comment = null
    }

}

@Entity
@Table(name = "Image")
class Image(

    @Lob
    @Column
    val image: ByteArray? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    var comment: TaskComment?

): BaseEntity()


@Entity
@Table(name = "Tag")
class Tag(

    @Column
    val name: String,

    @ManyToMany(mappedBy = "tags")
    val tasks: MutableSet<Task> = mutableSetOf()

) : BaseEntity()


@Entity
@Table(name = "TaskGroup")
class TaskGroup(

    @Column
    val title: String,

    @Column
    val description: String,

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val tasks: MutableSet<Task> = mutableSetOf()

) : BaseEntity() {

    fun addTask(task: Task) {
        tasks.add(task)
        task.group = this
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
        task.group = null
    }

}


@Entity
@Table(name = "Role")
class Role(
    val role: String,

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<User> = mutableSetOf()

) : BaseEntity()


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