package com.github.gedzeppelin.kotlinutils.util

import android.content.res.ColorStateList
import android.content.res.Resources
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import dev.jorgecastillo.androidcolorx.library.asArgb
import dev.jorgecastillo.androidcolorx.library.asColorInt

fun makeContainedTextColor(onPrimaryColor: Int, colorOnSurface: Int) = ColorStateList(
    arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf()
    ),
    intArrayOf(
        onPrimaryColor,
        colorOnSurface.asArgb().copy(alpha = 97).asColorInt()
    )
)

fun makeContainedBackgroundColor(primaryColor: Int, colorOnSurface: Int) = ColorStateList(
    arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf()
    ),
    intArrayOf(
        primaryColor,
        colorOnSurface.asArgb().copy(alpha = 31).asColorInt()
    )
)

fun makeContainedRippleColor(onPrimaryColor: Int): ColorStateList {
    val onPrimaryColor61 = onPrimaryColor.asArgb().copy(alpha = 61).asColorInt()
    return ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused, android.R.attr.state_hovered),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_hovered),
            intArrayOf()
        ), intArrayOf(
            onPrimaryColor61,
            onPrimaryColor61,
            onPrimaryColor61,
            onPrimaryColor.asArgb().copy(alpha = 20).asColorInt(),
            onPrimaryColor
        )
    )
}

fun makeFlatTextColor(primaryColor: Int, colorOnSurface: Int) = ColorStateList(
    arrayOf(
        intArrayOf(
            android.R.attr.state_checkable,
            android.R.attr.state_checked,
            android.R.attr.state_enabled
        ),
        intArrayOf(
            android.R.attr.state_checkable,
            -android.R.attr.state_checked,
            android.R.attr.state_enabled
        ),
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf()
    ),
    intArrayOf(
        primaryColor,
        colorOnSurface.asArgb().copy(alpha = 153).asColorInt(),
        primaryColor,
        colorOnSurface.asArgb().copy(alpha = 97).asColorInt()
    )
)

fun makeFlatBackgroundColor(primaryColor: Int, resources: Resources) = ColorStateList(
    arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
    ), intArrayOf(
        primaryColor.asArgb().copy(alpha = 20).asColorInt(),
        ResourcesCompat.getColor(resources, android.R.color.transparent, null)
    )
)

fun makeFlatRippleColor(primaryColor: Int): ColorStateList {
    val primaryColor31 = primaryColor.asArgb().copy(alpha = 31).asColorInt()
    return ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused, android.R.attr.state_hovered),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_hovered),
            intArrayOf()
        ), intArrayOf(
            primaryColor31,
            primaryColor31,
            primaryColor31,
            primaryColor.asArgb().copy(alpha = 10).asColorInt(),
            primaryColor
        )
    )
}