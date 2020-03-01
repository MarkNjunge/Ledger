package com.marknjunge.ledger.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marknjunge.ledger.BuildConfig
import com.marknjunge.ledger.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        tvVersionName.text = "Version ${BuildConfig.VERSION_NAME}"

        llSourceCode.setOnClickListener { openUrl("https://github.com/MarkNjunge/Ledger") }
        imgWebsite.setOnClickListener { openUrl("https://marknjunge.com") }
        imgGithub.setOnClickListener { openUrl("https://github.com/MarkNjunge") }
        imgLinkedIn.setOnClickListener { openUrl("https://linkedin.com/in/marknjunge") }
    }

    private fun openUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
