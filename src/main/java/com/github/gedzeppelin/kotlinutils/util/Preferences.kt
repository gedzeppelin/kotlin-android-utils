@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.squareup.moshi.Moshi

private const val UI_MODE_SHARED_PREFERENCES_KEY = "d7<bF9q(?Z-PX"

inline fun <reified T : Any> SharedPreferences.storeAsJson(key: String, payload: T) =
    edit { putString(key, payload.asJson()) }

inline fun <T : Context, reified S : Any> T.storeAsJson(spKey: String, key: String, payload: S) =
    getSharedPreferences(spKey, Context.MODE_PRIVATE).storeAsJson(key, payload)

inline fun <T : Activity, reified S : Any> T.storeAsJson(key: String, payload: S) =
    getPreferences(Context.MODE_PRIVATE).storeAsJson(key, payload)


fun SharedPreferences.removePreference(key: String) = edit { remove(key) }

/**
 * [Context] extension: Store the (JSON parsed) user credentials in the SharedPreferences.
 */
fun <T : Context> T.removePreference(spKey: String, key: String) =
    getSharedPreferences(spKey, Context.MODE_PRIVATE).removePreference(key)

fun <T : Activity> T.removePreference(key: String) =
    getPreferences(Context.MODE_PRIVATE).removePreference(key)


/**
 * [SharedPreferences] extension: Gets the (if exists) from the internal storage.
 *
 */
inline fun <reified T : Any> SharedPreferences.fromJson(key: String): T? =
    getString(key, null)?.let {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(T::class.java)
        try {
            jsonAdapter.fromJson(it)
        } catch (e: Exception) {
            null
        }
    }

/**
 * [Context] extension: Gets the [S] (if exists) from the internal storage.
 *
 * @return the current [S], if exists, otherwise null.
 *
 * @see SharedPreferences.fromJson
 */
inline fun <T : Context, reified S : Any> T.fromJson(spKey: String, key: String): S? =
    getSharedPreferences(spKey, Context.MODE_PRIVATE).fromJson(key)

inline fun <T : Activity, reified S : Any> T.fromJson(key: String): S? =
    getPreferences(Context.MODE_PRIVATE).fromJson(key)


/**
 * [AppCompatActivity] extension: change the DayNight mode of the application.
 *
 * @param uiMode the new DayNight boolean mode, true for dark, false for light.
 * @return the new DayNight boolean mode.
 */
fun <T : Activity> T.changeUiMode(spKey: String, uiMode: Int): Int {
    AppCompatDelegate.setDefaultNightMode(uiMode)
    getSharedPreferences(spKey, Context.MODE_PRIVATE).edit { putInt(UI_MODE_SHARED_PREFERENCES_KEY, uiMode) }
    return uiMode
}

fun <T : Activity> T.getStoredUiMode(spKey: String): Int =
    getSharedPreferences(spKey, Context.MODE_PRIVATE).getInt(UI_MODE_SHARED_PREFERENCES_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

fun <T : Activity> T.getUiMode(): Int = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
    Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_NO
    Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
}

fun Int.isNight(): Boolean = this == AppCompatDelegate.MODE_NIGHT_YES
fun Int.opposite(): Int {
    return if (this == AppCompatDelegate.MODE_NIGHT_YES) AppCompatDelegate.MODE_NIGHT_NO
    else AppCompatDelegate.MODE_NIGHT_YES
}
