@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.github.gedzeppelin.kotlinutils.R
import com.squareup.moshi.Moshi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * [Context] extension: gets a temporary [Uri] from a file in the cache directory.
 *
 *  @return the temporary uri.
 */
fun Context.getCacheUri(): Uri {
    var fileAux = File(cacheDir, "TEMP_CROP.jpg")
    var count = 0
    while (fileAux.exists()) {
        fileAux = File(cacheDir, "TEMP_CROP${count}.jpg")
        count += 1
    }
    return Uri.fromFile(fileAux)
}

fun Resources.getDps(sizeInDp: Int): Int = (sizeInDp * displayMetrics.density + 0.5f).toInt()

inline fun <reified T : Any> T.asJson(): String {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(T::class.java)
    return adapter.toJson(this)
}

fun <T: Context> T.getImageUri(bitmap: Bitmap?): Uri? {
    if (bitmap != null) {
        val file = createTempFile()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
        return file.toUri()
    }
    return null
}

val <T: EditText> T.value: String get() = this.text.toString()

fun <T : EditText> T.asRequest(): RequestBody =
    RequestBody.create(MediaType.parse("text/plain"), this.text.toString())

fun imagePart(name: String, file: File?): MultipartBody.Part? {
    if (file != null) {
        val request = RequestBody.create(MediaType.parse("image/*"), file)
        return MultipartBody.Part.createFormData(name, file.name, request)
    }
    return null
}

fun <T: Context> T.requireImagePart(name: String, file: File?): MultipartBody.Part {
    val image = if (file != null) file else {
        val drawable = ContextCompat.getDrawable(this, R.drawable.placeholder__1_1) as BitmapDrawable
        val tempFile = createTempFile()
        drawable.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, tempFile.outputStream())
        tempFile
    }
    val request = RequestBody.create(MediaType.parse("image/*"), image)
    return MultipartBody.Part.createFormData(name, image.name, request)
}

fun <T: Fragment> T.requireImagePart(name: String, file: File?): MultipartBody.Part =
    requireContext().requireImagePart(name, file)
