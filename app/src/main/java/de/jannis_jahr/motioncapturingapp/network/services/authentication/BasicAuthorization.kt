package de.jannis_jahr.motioncapturingapp.network.services.authentication

import android.util.Base64

class BasicAuthorization(private var username: String?, private var password: String?) {

    fun buildHeader(): String? {
        return "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
    }
}