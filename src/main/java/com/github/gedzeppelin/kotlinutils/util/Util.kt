@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.util

import com.squareup.moshi.Moshi


inline fun <reified T : Any> T.asJson(): String {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(T::class.java)
    return adapter.toJson(this)
}


