package de.jannis_jahr.motioncapturingapp.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is add Fragment"
    }
    val text: LiveData<String> = _text
}