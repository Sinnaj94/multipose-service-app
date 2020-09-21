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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm
    var context: Context? = null

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

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

    fun getCallback(host: String) : Callback<Token> {
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

    fun login(host: String, username: String, password: String) {

        loginRepository.login(host, username, password, getCallback(host))
    }

    fun register(host: String, username: String, password: String) {
        // can be launched in a separate asynchronous job
        /*val result = loginRepository.register(host, username, password)

        if (result is Result.Success<*>) {
            TODO()
            //_loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            TODO()
            //_loginResult.value = LoginResult(error = R.string.login_failed)
        }*/
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
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}