package com.library.system.repository

import com.library.system.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA repository for accessing and managing User entities.
 */
@Repository
interface UserRepository : JpaRepository<User, UUID> {
}
