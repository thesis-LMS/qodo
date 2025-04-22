package com.library.system.model

import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val role: UserRole = UserRole.MEMBER
)
