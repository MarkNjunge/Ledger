package com.marknjunge.ledger.utils

import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.data.models.AppUpdateDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

interface AppUpdate {
    suspend fun getLatestVersion(): AppUpdateDetail?

    fun shouldUpdate(ignoreSkip: Boolean): Boolean
}

class AppUpdateImpl(private val appPreferences: AppPreferences) : AppUpdate {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getLatestVersion(): AppUpdateDetail? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://ledger.marknjunge.com/app-version.json")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = readStream(httpURLConnection.inputStream)
                    val jsonObject = JSONObject(stream)
                    val latestVersion = jsonObject["latestVersion"] as Int
                    val latestVersionUrl = jsonObject["latestVersionUrl"] as String
                    appPreferences.latestVersion = latestVersion
                    AppUpdateDetail(latestVersion, latestVersionUrl)

                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
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

    private fun readStream(inStream: InputStream): String {
        var reader: BufferedReader? = null
        val response = StringBuffer()
        try {
            reader = BufferedReader(InputStreamReader(inStream))
            var line: String? = ""
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return response.toString()
    }

}