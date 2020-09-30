@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.util

import androidx.core.text.HtmlCompat

fun <T: CharSequence> T.asBold(): CharSequence {
    val boldText = "<b>$this</b>"
    return HtmlCompat.fromHtml(boldText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun <T: CharSequence> T.asItalic(): CharSequence {
    val boldText = "<i>$this</i>"
    return HtmlCompat.fromHtml(boldText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}