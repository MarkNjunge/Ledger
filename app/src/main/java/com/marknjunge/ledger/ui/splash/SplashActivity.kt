package com.marknjunge.ledger.ui.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.marknjunge.ledger.R
import com.marknjunge.ledger.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    private val REQUEST_READ_SMS: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_SMS) {
            if (isPermissionGranted(grantResults)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun Context.isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}
