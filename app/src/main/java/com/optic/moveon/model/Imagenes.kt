package com.optic.moveon.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object Imagenes {
    fun saveImage(context: Context, filename: String, bitmap: Bitmap): Boolean {
        if (context == null || filename.isEmpty() || bitmap == null) {
            return false
        }

        return try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadImage(context: Context, filename: String): Bitmap? {
        if (context == null || filename.isEmpty()) {
            return null
        }

        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }
}

