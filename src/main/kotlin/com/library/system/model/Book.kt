package com.library.system.model

import java.time.LocalDate
import java.util.UUID

data class Book(
    val id: UUID = UUID.randomUUID(),
    var title: String,
    val author: String,
    var available: Boolean = true,
    var borrowedByUserId: UUID? = null,
    var dueDate: LocalDate? = null
)