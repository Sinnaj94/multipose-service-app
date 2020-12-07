package de.jannis_jahr.motioncapturingapp.network.services.model

data class JobStatistics(
        val success: Int,
        val pending: Int,
        val failed: Int
)