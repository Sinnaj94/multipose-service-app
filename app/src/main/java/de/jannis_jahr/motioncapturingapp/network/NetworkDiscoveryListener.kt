package de.jannis_jahr.motioncapturingapp.network

interface NetworkDiscoveryListener {
    fun notify(value: String?)
    fun notifyProgress(progress: Int)
}