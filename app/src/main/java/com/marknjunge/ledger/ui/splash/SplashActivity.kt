package com.marknjunge.ledger.ui.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    private val REQUEST_READ_SMS: Int = 1
    private val messagesRepository: MessagesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_SMS) {
            if (isPermissionGranted(grantResults)) {
                fetchMessages()
            } else {
                Timber.d("Permission not granted")
                setContentView(R.layout.activity_splash)

                btnGrantPermission.setOnClickListener {
                    checkPermissions()
                }
            }
        }
    }

    private fun checkPermissions() {
        if (isPermissionGranted(Manifest.permission.READ_SMS)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_READ_SMS)
        } else {
            fetchMessages()
        }
    }

    private fun fetchMessages() {
        lifecycleScope.launch {
            messagesRepository.fetchMessages()
            proceed()
        }
    }

    private fun proceed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun Context.isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}
