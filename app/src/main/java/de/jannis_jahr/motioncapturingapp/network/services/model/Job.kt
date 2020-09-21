package de.jannis_jahr.motioncapturingapp.network.services.model

data class Job (
    val id: String,
    val user_id: String,
    val date_updated: String,
    val results: Array<Result>
)
