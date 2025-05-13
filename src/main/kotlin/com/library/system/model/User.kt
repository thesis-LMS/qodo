package com.library.system.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table // Optional: if your table name is different or to specify schema etc.
import java.util.UUID

@Entity
@Table(name = "users") // It's good practice to explicitly name tables, "user" can be a reserved word in SQL
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String, // Consider adding @Column(unique=true) if email should be unique
    @Enumerated(EnumType.STRING) // Store enum as string
    val role: UserRole = UserRole.MEMBER,
)
