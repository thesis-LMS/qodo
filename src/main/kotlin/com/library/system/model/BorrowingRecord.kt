package com.library.system.model

import java.util.UUID

data class BorrowingRecord (val id: UUID, val bookId: UUID, val userId: UUID) {
}