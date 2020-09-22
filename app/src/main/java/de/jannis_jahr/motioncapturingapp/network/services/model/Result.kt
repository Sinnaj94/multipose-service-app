package de.jannis_jahr.motioncapturingapp.network.services.model

data class Result(
    val id: String,
    val result_code: Int,
    val date: String,
    val output_video_url: String,
    val output_bvh: String
)