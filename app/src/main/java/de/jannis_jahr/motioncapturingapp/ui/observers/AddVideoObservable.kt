package de.jannis_jahr.motioncapturingapp.ui.observers

interface AddVideoObservable {
    val observers: ArrayList<AddVideoObserver>

    fun add(observer: AddVideoObserver) {
        observers.add(observer)
    }

    fun remove(observer: AddVideoObserver) {
        observers.remove(observer)
    }

    fun sendUpdateEvent() {
        observers.forEach { it.update() }
    }
}