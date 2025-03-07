package ru.tasktracker.taskservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.Objects


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @Id
    @Column(name = "id", updatable = false)
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
        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}