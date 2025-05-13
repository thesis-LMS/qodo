package com.library.system.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity // Added
data class Book(
    @Id // Added
    val id: UUID = UUID.randomUUID(),
    var title: String,
    val author: String,
    var available: Boolean = true,
    @Column(nullable = true) // Explicitly allow null for foreign key type
    var borrowedByUserId: UUID? = null,
    @Column(nullable = true)
    var dueDate: LocalDate? = null,
)
