package de.jannis_jahr.motioncapturingapp.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import de.jannis_jahr.motioncapturingapp.IntroActivity
import de.jannis_jahr.motioncapturingapp.MainActivity
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.NetworkDiscoveryListener
import de.jannis_jahr.motioncapturingapp.network.NetworkDiscovery
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), NetworkDiscoveryListener {

    private lateinit var loginViewModel: LoginViewModel
    var discovery : NetworkDiscovery? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE)
        if(!prefs.getBoolean(ApplicationConstants.PREFERENCE_INTRO_FINISHED, false)) {
            val i = Intent(applicationContext, IntroActivity::class.java)
            startActivity(i)
        }
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        server_address.setText(getSharedPreferences(ApplicationConstants.PREFERENCES, MODE_PRIVATE)
            .getString("hostname", ""))



        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // Give context
        loginViewModel.context = applicationContext
        if(loginViewModel.tokenExists()) {
            loading.visibility = View.VISIBLE
            loginViewModel.login()
        }
        login.isEnabled = false
        register.isEnabled = false


        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            register.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
        })

        loginViewModel.registrationResult.observe(this@LoginActivity, Observer {
            val registrationResult = it?:return@Observer

            loading.visibility = View.GONE
            register.isEnabled = true

            if(registrationResult.error != null) {
                showRegistrationFailed(registrationResult.error)
            }
            if(registrationResult.success != null) {
                val snackbar = Snackbar.make(container, "Registered user ${registrationResult.success.displayName}.", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            server_address.text.toString(),
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(server_address.text.toString(), username.text.toString(), password.text.toString())
            }

            register.setOnClickListener {
                loading.visibility = View.VISIBLE
                register.isEnabled = false
                loginViewModel.register(server_address.text.toString(), username.text.toString(), password.text.toString())
            }
        }

        // Search server
        search_for_server.setOnCheckedChangeListener{ _, state ->
            if(state) {
                startServerDiscovery()
            } else {
                stopServerDiscovery()
            }
        }
    }

    private fun showRegistrationFailed(error: Int) {
        val snackbar = Snackbar.make(container, "Registration failed.", Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    private fun startServerDiscovery() {
        Toast.makeText(this, R.string.searching_for_server, Toast.LENGTH_SHORT).show()
        server_address.setText("")
        server_address.hint = getString(R.string.searching_for_server)
        server_address.isEnabled = false
        // Suspend old task
        discovery?.running = false
        // Create thread.
        discovery = NetworkDiscovery()
        search_progress.visibility = View.VISIBLE
        search_progress.progress = 0

        discovery?.listener = this
        Thread(discovery).start()
    }

    private fun stopServerDiscovery() {
        discovery?.running = false
        server_address.isEnabled = true
        server_state.visibility = View.GONE
        search_progress.visibility = View.GONE
        server_address.isEnabled = true
        server_address.hint = getString(R.string.prompt_server_address)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun notify(value: String?) {
        Toast.makeText(applicationContext, "Found network $value.", Toast.LENGTH_SHORT).show()
        search_for_server.isChecked = false
        server_address.setText(value)
    }

    override fun notifyProgress(progress: Int) {
        search_progress.progress = ((progress.toFloat() / 255f) * 100).toInt()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}