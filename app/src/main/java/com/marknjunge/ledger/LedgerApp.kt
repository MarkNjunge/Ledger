package com.marknjunge.ledger

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.marknjunge.ledger.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

@Suppress("unused")
class LedgerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.BUILD_TYPE == "debug") {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "Timber/${element.fileName.substringBefore(".")}.${element.methodName}(Ln${element.lineNumber})"
                }
            })
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }

        startKoin {
            androidContext(this@LedgerApp)
            modules(appModule)
        }
    }
}