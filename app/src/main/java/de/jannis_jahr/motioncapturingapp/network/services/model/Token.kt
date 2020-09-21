package de.jannis_jahr.motioncapturingapp.network.services.model

data class Token (
    val duration: Int,
    val token: String,
    val user: User,
    val exp: Float
)