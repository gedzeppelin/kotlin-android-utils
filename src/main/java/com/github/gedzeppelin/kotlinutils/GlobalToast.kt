@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

class GlobalToast {
    companion object {
        private lateinit var toast: Toast

        private fun makeSnackbar(context: Context, message: CharSequence, length: Int) {
            val toast0 = Toast.makeText(context, message, length)

            if (::toast.isInitialized) toast.cancel()

            toast = toast0
            toast.show()
        }

        private fun makeSnackbar(context: Context, message: Int, length: Int) {
            val toast0 = Toast.makeText(context, message, length)

            if (::toast.isInitialized) toast.cancel()

            toast = toast0
            toast.show()
        }

        @JvmStatic
        fun <T : Context> T.longToast(message: CharSequence) =
            makeSnackbar(this, message, Toast.LENGTH_LONG)

        @JvmStatic
        fun <T : Context> T.longToast(message: Int) =
            makeSnackbar(this, message, Toast.LENGTH_LONG)

        @JvmStatic
        fun <T : Context> T.toast(message: CharSequence) =
            makeSnackbar(this, message, Toast.LENGTH_SHORT)

        @JvmStatic
        fun <T : Context> T.toast(message: Int) =
            makeSnackbar(this, message, Toast.LENGTH_SHORT)


        @JvmStatic
        fun <T : Fragment> T.longToast(message: CharSequence) =
            makeSnackbar(requireContext(), message, Toast.LENGTH_LONG)

        @JvmStatic
        fun <T : Fragment> T.longToast(message: Int) =
            makeSnackbar(requireContext(), message, Toast.LENGTH_LONG)

        @JvmStatic
        fun <T : Fragment> T.toast(message: CharSequence) =
            makeSnackbar(requireContext(), message, Toast.LENGTH_SHORT)

        @JvmStatic
        fun <T : Fragment> T.toast(message: Int) =
            makeSnackbar(requireContext(), message, Toast.LENGTH_SHORT)


        @JvmStatic
        fun <T : View> T.longToast(message: CharSequence) =
            makeSnackbar(context, message, Toast.LENGTH_LONG)

        @JvmStatic
        fun <T : View> T.longToast(message: Int) =
            makeSnackbar(context, message, Toast.LENGTH_LONG)

        @JvmStatic
        fun <T : View> T.toast(message: CharSequence) =
            makeSnackbar(context, message, Toast.LENGTH_SHORT)

        @JvmStatic
        fun <T : View> T.toast(message: Int) =
            makeSnackbar(context, message, Toast.LENGTH_SHORT)
    }
}