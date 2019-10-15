package com.marknjunge.ledger.utils

import android.content.Context
import android.os.Environment
import com.marknjunge.ledger.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

class LocalStorage(private val storageDir: File, val isExternal: Boolean) {
    companion object {
        fun getInternal(context: Context): LocalStorage = LocalStorage(context.filesDir, false)

        fun getExternal(context: Context): LocalStorage {
            val applicationInfo =
                context.packageManager.getApplicationInfo(BuildConfig.APPLICATION_ID, 0)

            if (applicationInfo.targetSdkVersion >= 29) {
                Timber.w("Scoped Storage will be required in future.")
                Timber.w("See https://developer.android.com/training/data-storage/files/external-scoped#opt-out-of-scoped-storage")
            }
            @Suppress("DEPRECATION")
            return LocalStorage(Environment.getExternalStorageDirectory(), true)
        }

        // TODO Use a fragment to make scoped storage work
        // https://developer.android.com/guide/topics/providers/document-provider#client
        // https://github.com/sagar-viradiya/eazypermissions/blob/master/common/src/main/java/com/eazypermissions/common/BasePermissionManager.kt
    }

    fun listFiles(path: String = "/"): Array<File> {
        val filename = storageDir.path + path
        val file = File(filename)
        return file.listFiles() ?: arrayOf()
    }

    fun writeToFile(
        filename: String,
        text: String,
        append: Boolean = false,
        charset: Charset = Charsets.UTF_8
    ) {
        val file = File(storageDir, filename)
        if (append) {
            file.appendText(text, charset)
        } else {
            file.writeText(text)
        }
    }

    fun writeToFile(filename: String, content: ByteArray, append: Boolean = false) {
        val file = File(storageDir, filename)
        if (append) {
            file.appendBytes(content)
        } else {
            file.writeBytes(content)
        }
    }

    @Throws(FileNotFoundException::class)
    fun getFile(filename: String): File = File(storageDir, filename)

    fun createFolder(folderName: String): Boolean {
        val storageFolder = getFile(folderName)
        return storageFolder.exists() || storageFolder.mkdirs()
    }

    fun deleteFile(filename: String) {
        val file = getFile(filename)
        if (file.exists()) {
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
        }
    }
}