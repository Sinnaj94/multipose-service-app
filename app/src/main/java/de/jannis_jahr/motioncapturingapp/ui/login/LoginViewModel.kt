package de.jannis_jahr.motioncapturingapp.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import de.jannis_jahr.motioncapturingapp.data.LoginRepository

import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.data.model.LoggedInUser
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm
    var context: Context? = null

    private val _loginResult = MutableLiveData<LoginResult>()
    private val _registrationResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    val registrationResult: LiveData<LoginResult> = _registrationResult

    fun tokenExists(): Boolean {
        context?.let {
            if(loginRepository.loadConnection(it)) {
                return true
            }
        }
        return false
    }

    fun login(): Boolean {
        context?.let {
            loginRepository.loadConnection(it)
            if(loginRepository.isLoggedIn) {
                loginRepository.login(loginRepository.user!!.token, loginRepository.user!!.host, getCallback(loginRepository.user!!.host))
                return true
            }
        }
        return false
    }

    private fun getCallback(host: String) : Callback<Token> {
        return object: Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(response.isSuccessful) {
                    // Save username and password
                    _loginResult.value = LoginResult(success = LoggedInUserView(displayName = response.body()!!.user.username))
                    loginRepository.setConnection(LoggedInUser(response.body()!!.token, host), context!!)
                    //loginRepository.setConnection(response.body()!!.token)
                } else {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                t.printStackTrace()
                _loginResult.value = LoginResult(error = R.string.network_error)
            }
        }
    }

    fun getRegistrationCallback() : Callback<User> {
        return object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 200) {
                    _registrationResult.value = LoginResult(success = LoggedInUserView(displayName = response.body()!!.username))
                } else {
                    _registrationResult.value = LoginResult(error = R.string.user_exists)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _registrationResult.value = LoginResult(error = R.string.network_error)
            }
        }
    }

    fun login(host: String, username: String, password: String) {

        loginRepository.login(host, username, password, getCallback(host))
    }

    fun register(host: String, username: String, password: String) {
        // can be launched in a separate asynchronous job
        loginRepository.register(host, username, password, getRegistrationCallback())
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.length >= 5

    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 5
    }
}