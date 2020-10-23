package de.jannis_jahr.motioncapturingapp.network.services.model

import java.time.LocalDateTime
import java.util.*

data class Bookmark (
    val category: String,
    val user_id: UUID,
    val job_id: UUID,
    val count: UUID
)
