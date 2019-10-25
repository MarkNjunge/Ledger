package com.marknjunge.ledger.utils

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import java.io.FileOutputStream
import java.lang.Exception

// https://developer.android.com/guide/topics/providers/document-provider#create
object SAFUtils {
    fun getIntent(mimeType: String, filename: String) =
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as
            // a file (as opposed to a list of contacts or timezones).
            addCategory(Intent.CATEGORY_OPENABLE)

            type = mimeType
            putExtra(Intent.EXTRA_TITLE, filename)
        }

    fun writeContent(
        contentResolver: ContentResolver,
        uri: Uri,
        content: ByteArray,
        mode: String = "w"
    ) {
        contentResolver.openFileDescriptor(uri, mode)?.let { fileDescriptor ->
            FileOutputStream(fileDescriptor.fileDescriptor).use { fileOutputStream ->
                fileOutputStream.write(content)
            }
        } ?: throw Exception("Content Resolver returned null")
    }

}