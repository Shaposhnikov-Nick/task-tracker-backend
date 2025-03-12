package ru.tasktracker.taskservice.entity

import cz.encircled.skom.Convertable
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "`Users`")
class User(

    @Column
    val login: String,

    @Column
    val password: String,

    @Column(name = "email_confirmed")
    var emailConfirmed: Boolean,

    @Column
    var blocked: Boolean,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "`UserRole`",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    var profile: UserProfile,

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    val tasks: MutableSet<Task> = mutableSetOf()

) : BaseEntity(), Convertable {

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
@Table(name = "`UserProfile`")
class UserProfile(

    @OneToOne(mappedBy = "profile")
    val user: User? = null,

    @Column
    var name: String,

    @Column(name = "last_name")
    var lastName: String,

    @Column
    var birthday: LocalDateTime,

    @Column
    var email: String,

    @Column
    var about: String?,

    @Lob
    @Column
    val avatar: ByteArray?

) : BaseEntity(), Convertable

@Entity
@Table(name = "`Role`")
class Role(

    val name: String,

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<User> = mutableSetOf()

) : BaseEntity(), Convertable
