package com.marknjunge.ledger.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.marknjunge.ledger.BuildConfig
import com.marknjunge.ledger.data.local.AppPreferences
import org.koin.core.KoinComponent
import timber.log.Timber

interface AppUpdate {
    fun getLatestVersion(onComplete: (version: Int, url: String) -> Unit)

    fun shouldUpdate(ignoreSkip: Boolean): Boolean
}

class AppUpdateImpl(
    private val remoteConfig: FirebaseRemoteConfig,
    private val appPreferences: AppPreferences
) : AppUpdate {
    private val fetchInterval = if (BuildConfig.DEBUG) 0L else 3600L

    override fun getLatestVersion(onComplete: (version: Int, url: String) -> Unit) {
        remoteConfig.fetch(fetchInterval)
            .addOnSuccessListener {
                remoteConfig.activate()
                val latestVersion = remoteConfig.getString("latest_version").toInt()
                val latestVersionUrl = remoteConfig.getString("latest_version_url")
                appPreferences.latestVersion = latestVersion
                onComplete(latestVersion, latestVersionUrl)
            }
            .addOnFailureListener { e ->
                Timber.e(e.message)
                val latestVersionUrl = remoteConfig.getString("latest_version_url")
                onComplete(appPreferences.latestVersion, latestVersionUrl)
            }
    }


    override fun shouldUpdate(ignoreSkip: Boolean): Boolean {
        return if (ignoreSkip) {
            appPreferences.currentVersion < appPreferences.latestVersion
        } else {
            if (appPreferences.latestVersion == appPreferences.skipUpdateVer) {
                false
            } else {
                appPreferences.currentVersion < appPreferences.latestVersion
            }
        }
    }

}