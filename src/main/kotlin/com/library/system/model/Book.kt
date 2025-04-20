package com.library.system.model

import java.util.UUID

data class Book(val id: UUID, var title: String, val author: String, private var available: Boolean) {
    var isAvailable: Boolean = false
}