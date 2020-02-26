package com.marknjunge.ledger.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.marknjunge.ledger.BuildConfig
import com.marknjunge.ledger.data.local.AppPreferences
import org.koin.core.KoinComponent
import timber.log.Timber

object AppUpdate : KoinComponent {
    fun getLatestVersion(
        remoteConfig: FirebaseRemoteConfig,
        appPreferences: AppPreferences,
        onComplete: (version: Int) -> Unit
    ) {
        val fetchInterval = if (BuildConfig.DEBUG) 0L else 3600L
        remoteConfig.fetch(fetchInterval)
            .addOnSuccessListener {
                remoteConfig.activate()
                val latestVersion = remoteConfig.getString("latest_version").toInt()
                appPreferences.latestVersion = latestVersion
                onComplete(latestVersion)
            }
            .addOnFailureListener { e ->
                Timber.e(e.message)
                onComplete(appPreferences.latestVersion)
            }
    }

    fun shouldUpdate(appPreferences: AppPreferences, ignoreSkip: Boolean): Boolean {
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