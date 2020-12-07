package de.jannis_jahr.motioncapturingapp.ui.view_holders

import android.os.Handler
import android.os.Looper
import de.jannis_jahr.motioncapturingapp.network.services.model.Job

class JobViewHolder(var job: Job) {
    var imageBinderHandler : Handler
    var progressHandler : Handler

    init {
        this.imageBinderHandler = Handler(Looper.getMainLooper())
        this.progressHandler = Handler(Looper.getMainLooper())
    }

    fun removeHandlers() {
        imageBinderHandler.removeCallbacksAndMessages(null)
        progressHandler.removeCallbacksAndMessages(null)
    }
}