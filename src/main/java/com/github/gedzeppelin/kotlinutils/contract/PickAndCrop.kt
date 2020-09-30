@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.contract

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

/**
 * [T] activity helper class for pick and crop an image from native gallery.
 *
 * @param T the type of the [ComponentActivity] to which the activity result is registered.
 * @param activity to which the activity result is registered.
 * @param x the X axis crop aspect ratio.
 * @param y the Y axis crop aspect ratio.
 * @param callback the lambda callback, will be invoked after successful pick and crop.
 *
 * @property imageCropper
 */
class PickCropImageActivity<T : ComponentActivity>(
    activity: T,
    x: Float,
    y: Float,
    callback: (uri: Uri) -> Unit
) {
    private val imagePicker = activity.registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) imageCropper.launch(it)
    }

    private val imageCropper = activity.registerForActivityResult(Ucrop(x, y)) {
        if (it != null) callback.invoke(it)
    }

    fun launch() {
        imagePicker.launch("image/*")
    }
}

/**
 * [T] fragment helper class for pick and crop an image from native gallery.
 *
 * @param T the type of the [Fragment] to which the activity result is registered.
 * @param fragment to which the activity result is registered.
 * @param x the X axis crop aspect ratio.
 * @param y the Y axis crop aspect ratio.
 * @param callback the lambda callback, will be invoked after successful pick and crop.
 */
class PickCropImageFragment<T : Fragment>(
    fragment: T,
    x: Float,
    y: Float,
    callback: (uri: Uri) -> Unit
) {
    private val imagePicker = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) imageCropper.launch(it)
    }

    private val imageCropper = fragment.registerForActivityResult(Ucrop(x, y)) {
        if (it != null) callback.invoke(it)
    }

    fun launch() {
        imagePicker.launch("image/*")
    }
}

/**
 * Starts an activity for pick and crop an image from native gallery.
 *
 * @param x the X axis crop aspect ratio.
 * @param y the Y axis crop aspect ratio.
 * @param callback to execute after successful pick and crop.
 *
 * @return the [PickCropImageActivity] contract.
 */
fun <T : ComponentActivity> T.registerForPickCropImage(
    x: Float = 1f,
    y: Float = 1f,
    callback: (uri: Uri) -> Unit
) = PickCropImageActivity(this, x, y, callback)

/**
 * Starts an activity for pick and crop an image from native gallery.
 *
 * @param x the X axis crop aspect ratio.
 * @param y the Y axis crop aspect ratio.
 * @param callback to execute after successful pick and crop.
 *
 * @return the [PickCropImageFragment] contract.
 */
fun <T : Fragment> T.registerForPickCropImage(
    x: Float = 1f,
    y: Float = 1f,
    callback: (uri: Uri) -> Unit
) = PickCropImageFragment(this, x, y, callback)