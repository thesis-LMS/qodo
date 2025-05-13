package com.library.system.repository

import com.library.system.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> { // Changed this line
    // JpaRepository provides findById, save, etc.
    // If you need custom queries like findByEmail, you can add them here.
    // fun findByEmail(email: String): Optional<User>
}
