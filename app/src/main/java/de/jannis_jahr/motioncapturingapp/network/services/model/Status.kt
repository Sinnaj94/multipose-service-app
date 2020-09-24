package de.jannis_jahr.motioncapturingapp.network.services.model

data class Status (
    val finished: Boolean,
    val problem: Boolean,
    val stage: Stage
)
