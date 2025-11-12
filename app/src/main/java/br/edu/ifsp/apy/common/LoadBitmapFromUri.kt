package br.edu.ifsp.apy.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build

fun loadBitmapFromUri(context: Context, uri: Uri, maxSize: Int = 1024): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {

        // MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Não foi possível abrir o URI: $uri")

        // Lê dimensões para redimensionar antes de carregar
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        // Calcula fator de escala para não estourar memória
        var scale = 1
        while (options.outWidth / scale > maxSize || options.outHeight / scale > maxSize) {
            scale *= 2
        }

        // Decodifica a imagem com redução proporcional
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = scale }
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, decodeOptions)
                ?: throw IllegalArgumentException("Falha ao decodificar bitmap de $uri")
        } ?: throw IllegalArgumentException("Não foi possível reabrir o URI: $uri")
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}