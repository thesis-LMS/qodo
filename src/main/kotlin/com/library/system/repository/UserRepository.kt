package com.library.system.repository

import com.library.system.model.User
import java.util.UUID
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository {
    fun findById(id: UUID): Optional<User>
    fun save(user: User): User
}
