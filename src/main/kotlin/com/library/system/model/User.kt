package com.library.system.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

/**
 * Represents a user (member, librarian, or admin) of the library system.
 * Uses UUID as the primary key.
 * This class will be mapped to the 'users' table in the database.
 */
@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @field:NotEmpty(message = "User name cannot be empty")
    @Column(nullable = false)
    var name: String,

    @field:NotEmpty(message = "Email cannot be empty")
    @field:Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    var email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.MEMBER
) {
}
