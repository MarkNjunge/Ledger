package com.marknjunge.ledger.data.local

import android.content.Context
import com.marknjunge.ledger.BuildConfig
import com.marknjunge.ledger.utils.PreferenceUtils

interface AppPreferences {
    var currentVersion: Int
    var latestVersion: Int
    var skipUpdateVer: Int
}

class AppPreferencesImpl(private val context: Context) : AppPreferences {
    private val prefutils by lazy {
        PreferenceUtils(
            context.getSharedPreferences(
                BuildConfig.APPLICATION_ID,
                Context.MODE_PRIVATE
            )
        )
    }

    override var currentVersion: Int
        get() = prefutils.get(CURRENT_VERSION, BuildConfig.VERSION_CODE)
        set(value) {
            prefutils.set(CURRENT_VERSION, value)
        }

    override var latestVersion: Int
        get() = prefutils.get(LATEST_VERSION, BuildConfig.VERSION_CODE)
        set(value) {
            prefutils.set(LATEST_VERSION, value)
        }

    override var skipUpdateVer: Int
        get() = prefutils.get(SKIP_UPDATE_VER, BuildConfig.VERSION_CODE)
        set(value) {
            prefutils.set(SKIP_UPDATE_VER, value)
        }

    companion object {
        private const val CURRENT_VERSION = "current_version"
        private const val LATEST_VERSION = "latest_version"
        private const val SKIP_UPDATE_VER = "skip_update_version"
    }
}