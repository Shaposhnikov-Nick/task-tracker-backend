package ru.tasktracker.taskservice.entity

import cz.encircled.skom.Convertable
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "`Task`")
class Task(

    @Column
    var title: String,

    @Column
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    var group: TaskGroup?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "parent_id")
    val parentId: Long?,

    @Enumerated(EnumType.STRING)
    var status: TaskStatus,

    @Enumerated(EnumType.STRING)
    var priority: TaskPriority,

    @Column(name = "`deadLine`")
    var deadLine: LocalDateTime,

    @Column(name = "assignee_id")
    var assigneeId: Long,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "`TaskTag`",
        joinColumns = [JoinColumn(name = "task_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")]
    )
    val tags: MutableSet<Tag> = mutableSetOf(),

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val taskComments: MutableSet<TaskComment> = mutableSetOf()

) : BaseEntity(), Convertable {

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
@Table(name = "`TaskComment`")
class TaskComment(

    @Column
    val description: String,

    @Column(name = "user_id")
    val userId: Long,

    @OneToMany(mappedBy = "comment", fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    val images: MutableSet<Image> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    var task: Task?

) : BaseEntity(), Convertable {

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
@Table(name = "`Image`")
class Image(

    @Lob
    @Column
    val image: ByteArray? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    var comment: TaskComment?

) : BaseEntity(), Convertable


@Entity
@Table(name = "`Tag`")
class Tag(

    @Column
    val name: String,

    @ManyToMany(mappedBy = "tags")
    val tasks: MutableSet<Task> = mutableSetOf()

) : BaseEntity(), Convertable


@Entity
@Table(name = "`TaskGroup`")
class TaskGroup(

    @Column
    val title: String,

    @Column
    val description: String,

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    val tasks: MutableSet<Task> = mutableSetOf()

) : BaseEntity(), Convertable {

    fun addTask(task: Task) {
        tasks.add(task)
        task.group = this
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
        task.group = null
    }

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