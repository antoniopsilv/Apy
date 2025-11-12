package br.edu.ifsp.apy.common

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createImageUri(context: Context): Uri {
    val imageFile = File.createTempFile(
        "photo_", ".jpg",
        context.getExternalFilesDir("Pictures")
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}