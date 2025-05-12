package com.library.system.model

/**
 * Represents the different roles a user can have within the library system.
 */
enum class UserRole {
    MEMBER,    // standard library member with borrowing rights
    LIBRARIAN, // staff member who can manage books and users
    ADMIN      // administrator with full system access
}
