package de.jannis_jahr.motioncapturingapp.ui.view

interface JobListTagsObserver {
    fun notifyAdd(tag: String)
    fun notifyRemove(tag: String)
}

interface JobListTagsObservable {
    var observer : JobListTagsObserver?
}