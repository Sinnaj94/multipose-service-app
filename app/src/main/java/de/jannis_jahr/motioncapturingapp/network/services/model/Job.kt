package de.jannis_jahr.motioncapturingapp.network.services.model

import java.time.LocalDateTime
import java.util.*

data class Job (
    val id: UUID,
    val name: String,
    val video_uploaded: Boolean,
    val date_updated: Date,
    val input_video_url: String,
    val thumbnail_url: String,
    val result: Result
) {
    override fun equals(other: Any?): Boolean {
        if(other is Job) {
            return id == other.id
        }
        return false
    }
}
