@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class GlobalSnackbar {
    companion object {
        private lateinit var snackbar: Snackbar

        private fun makeSnackbar(rootView: View, message: CharSequence, length: Int) {
            val snackbar1 = Snackbar.make(rootView, message, length)

            if (::snackbar.isInitialized) snackbar.dismiss()

            snackbar = snackbar1
            snackbar.setAction(R.string.snackbar_app_close) { snackbar.dismiss() }
            snackbar.show()
        }

        private fun makeSnackbar(rootView: View, message: Int, length: Int) {
            val snackbar1 = Snackbar.make(rootView, message, length)

            if (::snackbar.isInitialized) snackbar.dismiss()

            snackbar = snackbar1
            snackbar.setAction(R.string.snackbar_app_close) { snackbar.dismiss() }
            snackbar.show()
        }

        @JvmStatic
        private fun <T : Context> T.getRootView(): View {
            val activity = this as? Activity
            if (activity != null) {
                return activity.window.decorView.findViewById(android.R.id.content)
            }

            throw IllegalStateException("Context must be attached to an Activity")
        }

        @JvmStatic
        fun <T : Context> T.longSnackbar(message: CharSequence) =
            makeSnackbar(getRootView(), message, Snackbar.LENGTH_LONG)

        @JvmStatic
        fun <T : Context> T.longSnackbar(message: Int) =
            makeSnackbar(getRootView(), message, Snackbar.LENGTH_LONG)

        @JvmStatic
        fun <T : Context> T.snackbar(message: CharSequence) =
            makeSnackbar(getRootView(), message, Snackbar.LENGTH_SHORT)

        @JvmStatic
        fun <T : Context> T.snackbar(message: Int) =
            makeSnackbar(getRootView(), message, Snackbar.LENGTH_SHORT)



        @JvmStatic
        fun <T : Fragment> T.longSnackbar(message: CharSequence) =
            makeSnackbar(requireView(), message, Snackbar.LENGTH_LONG)

        @JvmStatic
        fun <T : Fragment> T.longSnackbar(message: Int) =
            makeSnackbar(requireView(), message, Snackbar.LENGTH_LONG)

        @JvmStatic
        fun <T : Fragment> T.snackbar(message: CharSequence) =
            makeSnackbar(requireView(), message, Snackbar.LENGTH_SHORT)

        @JvmStatic
        fun <T : Fragment> T.snackbar(message: Int) =
            makeSnackbar(requireView(), message, Snackbar.LENGTH_SHORT)



        @JvmStatic
        fun <T : View> T.longSnackbar(message: CharSequence) =
            makeSnackbar(rootView, message, Snackbar.LENGTH_LONG)

        @JvmStatic
        fun <T : View> T.longSnackbar(message: Int) =
            makeSnackbar(rootView, message, Snackbar.LENGTH_LONG)

        @JvmStatic
        fun <T : View> T.snackbar(message: CharSequence) =
            makeSnackbar(rootView, message, Snackbar.LENGTH_SHORT)

        @JvmStatic
        fun <T : View> T.snackbar(message: Int) =
            makeSnackbar(rootView, message, Snackbar.LENGTH_SHORT)
    }
}