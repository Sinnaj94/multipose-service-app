package de.jannis_jahr.motioncapturingapp.network.services.model

import java.time.LocalDateTime
import java.util.*

data class Job (
    val id: Int,
    val name: String,
    val description: String,
    val video_uploaded: Boolean,
    val date_updated: Date,
    val public: Boolean,
    val input_video_url: String,
    val thumbnail_url: String,
    val result: Result,
    val num_bookmarks: Int,
    val bookmarked: Boolean,
    val user: User,
    val tags: ArrayList<Tag>
) {
    override fun equals(other: Any?): Boolean {
        if(other is Job) {
            return id == other.id
        }
        return false
    }
}
