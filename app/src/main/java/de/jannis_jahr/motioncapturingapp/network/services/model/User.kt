package de.jannis_jahr.motioncapturingapp.network.services.model

import java.util.*

data class User(
    val username: String,
    val id: Int,
    val registration_date: Date
)