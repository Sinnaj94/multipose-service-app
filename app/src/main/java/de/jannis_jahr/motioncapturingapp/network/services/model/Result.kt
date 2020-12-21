package de.jannis_jahr.motioncapturingapp.network.services.model

import java.time.LocalDateTime
import java.util.*

data class Result(
    val id: Int,
    val result_code: Int,
    val date: Date,
    val max_people: Int,
    val output_video_url: String,
    val output_bvh: String
)