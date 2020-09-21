package de.jannis_jahr.motioncapturingapp.network.services.model

data class Result(
    val id: String,
    val job_id: String,
    val user_id: String,
    val result_code: String,
    val result_type: String,
    val person_id: Int?,
    val data: String?
)