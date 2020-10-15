package de.jannis_jahr.motioncapturingapp
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.login.LoginActivity

class IntroActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        val step1 = PaperOnboardingPage(
            getString(R.string.onboarding_1_title), getString(R.string.onboarding_1_text), getColor(
                R.color.orange_soda
            ),
            R.drawable.man_and_woman, R.drawable.ic_baseline_sentiment_very_satisfied_24
        )
        val step2 = PaperOnboardingPage(
            getString(R.string.onboarding_2_title), getString(R.string.onboarding_2_text), getColor(
                R.color.skyblue
            ),
            R.drawable.polaroid, R.drawable.ic_baseline_cloud_upload_24
        )
        val step3 = PaperOnboardingPage(
            getString(R.string.onboarding_3_title), getString(R.string.onboarding_3_text), getColor(
                R.color.jonqil
            ),
            R.drawable.atom, R.drawable.ic_baseline_arrow_forward_24
        )

        val onBoarding = PaperOnboardingFragment.newInstance(arrayListOf(step1, step2, step3))

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.onboarding_fragment, onBoarding)
        transaction.addToBackStack(null)
        transaction.commit()

        onBoarding.setOnRightOutListener {
            val editor = getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).edit()
            editor.putBoolean(ApplicationConstants.PREFERENCE_INTRO_FINISHED, true)
            editor.commit()
            finish()
        }
    }
}