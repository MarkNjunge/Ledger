package com.marknjunge.ledger.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.marknjunge.ledger.BuildConfig
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.data.models.ThemePreference
import kotlinx.android.synthetic.main.activity_about.*
import org.koin.android.ext.android.inject

class AboutActivity : AppCompatActivity() {

    private val appPreferences: AppPreferences by inject()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        tvVersionName.text = "Version ${BuildConfig.VERSION_NAME}"
        setThemeText()

        llSourceCode.setOnClickListener { openUrl("https://github.com/MarkNjunge/Ledger") }
        imgWebsite.setOnClickListener { openUrl("https://marknjunge.com") }
        imgGithub.setOnClickListener { openUrl("https://github.com/MarkNjunge") }
        imgLinkedIn.setOnClickListener { openUrl("https://linkedin.com/in/marknjunge") }

        llTheme.setOnClickListener {
            openThemePickerDialog()
        }
    }

    private fun setThemeText() {
        tvTheme.text = when (appPreferences.themePreference) {
            ThemePreference.LIGHT -> "Light"
            ThemePreference.DARK -> "Dark"
            ThemePreference.BATTERY -> "Battery Saver"
            ThemePreference.DEFAULT -> "System Default"
        }
    }

    private fun openUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    private fun openThemePickerDialog() {
        MaterialAlertDialogBuilder(this)
            .setItems(arrayOf("Light", "Dark", "Battery", "System Default")) { _, which ->
                when (which) {
                    0 -> {
                        appPreferences.themePreference = ThemePreference.LIGHT
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    1 -> {
                        appPreferences.themePreference = ThemePreference.DARK
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    2 -> {
                        appPreferences.themePreference = ThemePreference.BATTERY
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    }
                    3 -> {
                        appPreferences.themePreference = ThemePreference.DEFAULT
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
                setThemeText()
            }
            .setTitle("Select theme")
            .show()
    }
}
