@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.util

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

fun Intent.withArgs(block: Bundle.() -> Unit): Intent {
    val bundle = Bundle().apply(block)
    this.putExtras(bundle)
    return this
}

fun <F : Fragment> F.withArgs(block: Bundle.() -> Unit): F
    = this.apply { arguments = Bundle().apply(block) }

fun Bundle.withArgs(block: Bundle.() -> Unit): Bundle
    = Bundle().apply(block)

fun bundleWith(block: Bundle.() -> Unit): Bundle
    = Bundle().apply(block)

fun intentWith(block: Intent.() -> Unit): Intent
    = Intent().apply(block)