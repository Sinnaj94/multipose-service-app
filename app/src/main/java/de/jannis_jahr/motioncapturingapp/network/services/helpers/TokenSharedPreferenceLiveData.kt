package de.jannis_jahr.motioncapturingapp.network.services.helpers

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class TokenSharedPreferenceLiveData(private val sharedPreferences: SharedPreferences) : LiveData<String>() {

    private val mTokenSharedPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
            if (key == MYKEYSTRING) {
                value = sharedPreferences?.getString(MYKEYSTRING, "")
            }
        }


    override fun onActive() {
        super.onActive()
        value = sharedPreferences.getString(MYKEYSTRING, "")
        sharedPreferences.registerOnSharedPreferenceChangeListener(mTokenSharedPreferenceListener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mTokenSharedPreferenceListener)
    }

    companion object {
        private const val MYKEYSTRING = "token"
    }
}