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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.github.gedzeppelin.kotlinutils.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun <T : CharSequence> T.asBold(): CharSequence {
    val boldText = "<b>$this</b>"
    return HtmlCompat.fromHtml(boldText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun <T : CharSequence> T.asItalic(): CharSequence {
    val boldText = "<i>$this</i>"
    return HtmlCompat.fromHtml(boldText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

/**
 * [Context] extension: gets a temporary [Uri] for a file in the cache directory.
 *
 *  @return the temporary uri.
 */
fun Context.makeTempUri(prefix: String = "temp", suffix: String? = null): Uri {
    val fileAux = File.createTempFile(prefix, suffix)
    return Uri.fromFile(fileAux)
}

fun Resources.intAsDps(dps: Int): Int = (dps * displayMetrics.density + 0.5f).toInt()

fun <T : Bitmap?> T.asImageUri(format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): Uri? {
    if (this != null) {
        val file = createTempFile()
        compress(format, quality, file.outputStream())
        return file.toUri()
    }
    return null
}

fun makeImagePart(name: String, file: File?, mediaType: String = "image/*"): MultipartBody.Part? {
    if (file != null) {
        val request = RequestBody.create(MediaType.parse(mediaType), file)
        return MultipartBody.Part.createFormData(name, file.name, request)
    }
    return null
}

fun <T : Context> T.makeDefaultImagePart(
    name: String,
    file: File?,
    placeholder: Int = R.drawable.placeholder__1_1,
    mediaType: String = "image/*"
): MultipartBody.Part {
    val image = if (file != null) file else {
        val drawable = ContextCompat.getDrawable(this, placeholder) as BitmapDrawable
        val tempFile = createTempFile()
        drawable.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, tempFile.outputStream())
        tempFile
    }
    val request = RequestBody.create(MediaType.parse(mediaType), image)
    return MultipartBody.Part.createFormData(name, image.name, request)
}

fun <T : Fragment> T.makeDefaultImagePart(
    name: String, file: File?,
    placeholder: Int = R.drawable.placeholder__1_1,
    mediaType: String = "image/*"
): MultipartBody.Part = requireContext().makeDefaultImagePart(name, file, placeholder, mediaType)


val <T : EditText> T.value: String get() = this.text.toString()

fun <T : EditText> T.asRequestBody(mediaType: String = "text/plain"): RequestBody =
    RequestBody.create(MediaType.parse(mediaType), text.toString())
