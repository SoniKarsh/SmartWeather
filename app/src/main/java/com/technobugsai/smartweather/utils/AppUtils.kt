package com.technobugsai.smartweather.utils

import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

object AppUtils {

    fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeImage(str: String): Bitmap? {
        val imageBytes = Base64.decode(str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun getBytes(path: String): ByteArray {
        val file = File(path)
        val inputStream = FileInputStream(file)
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int

        while (inputStream.read(buffer).also { length = it } != -1) {
            outputStream.write(buffer, 0, length)
        }

        return outputStream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun returnCropped(resources: Resources, sourceBitmap: Bitmap): RoundedBitmapDrawable {
        val width = sourceBitmap.width
        val height = sourceBitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        paint.isAntiAlias = true

        val rect = Rect(0, 0, width, height)
        val rectF = RectF(rect)
        val radius = width / 2f

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(sourceBitmap, rect, rect, paint)

        sourceBitmap.recycle()

        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, outputBitmap)
        roundedBitmapDrawable.isCircular = true
        return roundedBitmapDrawable
    }

    inline fun <reified T> T.toJson(): String {
        return Gson().toJson(this, T::class.java)
    }

    inline fun <reified T> String.toObject(): T? {
        return try {
            Gson().fromJson(this, T::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    inline fun <reified T> String.toList(): List<T>? {
        return try {
            val listType = object : TypeToken<List<T>>() {}.type
            return Gson().fromJson(this, listType)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

}