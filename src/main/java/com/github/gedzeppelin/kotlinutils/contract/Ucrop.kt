@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.contract

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.github.gedzeppelin.kotlinutils.util.makeTempUri
import com.github.gedzeppelin.kotlinutils.util.withArgs
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity

/**
 * Ucrop activity contract helper class.
 *
 * @property x the X axis crop aspect ratio.
 * @property y the Y axis crop aspect ratio.
 */
class Ucrop(
    private val x: Float = 1f,
    private val y: Float = 1f
) : ActivityResultContract<Uri, Uri?>() {
    override fun createIntent(context: Context, input: Uri): Intent {
        return Intent(context, UCropActivity::class.java).withArgs {
            putParcelable(UCrop.EXTRA_INPUT_URI, input)
            putParcelable(UCrop.EXTRA_OUTPUT_URI, context.makeTempUri())
            putFloat(UCrop.EXTRA_ASPECT_RATIO_X, x)
            putFloat(UCrop.EXTRA_ASPECT_RATIO_Y, y)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == AppCompatActivity.RESULT_OK && intent != null) {
            val uri = UCrop.getOutput(intent)
            if (uri != null) return uri
        }
        return null
    }
}