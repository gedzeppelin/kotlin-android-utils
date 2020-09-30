package com.github.gedzeppelin.kotlinutils.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.databinding.ContainedProgressButtonBinding
import com.github.gedzeppelin.kotlinutils.databinding.FlatProgressButtonBinding
import com.github.gedzeppelin.kotlinutils.util.*
import com.google.android.material.button.MaterialButton

@Suppress("MemberVisibilityCanBePrivate")
class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    var ignoreByDelegate: Boolean

    val button: MaterialButton
    val progressBar: ProgressBar

    private val btnTextColor: ColorStateList
    private val transparentColor = ResourcesCompat.getColor(resources, android.R.color.transparent, null)

    var isLoading: Boolean = false
        set(value) {
            setLoadingState(value)
            field = value
        }

    var text: CharSequence
        get() = button.text
        set(value) {
            button.text = value
        }

    fun setText(resId: Int) {
        button.setText(resId)
    }

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0)
        val typedValue = TypedValue()

        // Obtain theme colors
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val primaryColor = typedValue.data

        context.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        val onPrimaryColor = typedValue.data

        context.theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        val colorOnSurface = typedValue.data

        // Obtain XML attributes
        val attrButtonTextColor: Int
        val attrButtonBackgroundTint = attributes.getColor(R.styleable.ProgressButton_backgroundTint, primaryColor)

        // Declare common variables
        val buttonBackgroundTint: ColorStateList
        val buttonRippleColor: ColorStateList

        // Get button type value and assign some variables
        val buttonType = attributes.getInt(R.styleable.ProgressButton_type, 0)
        when (buttonType) {
            0 -> {
                val binding = ContainedProgressButtonBinding.inflate(LayoutInflater.from(context), this)
                button = binding.cpbtnButton
                progressBar = binding.cpbtnProgressBar

                attrButtonTextColor = attributes.getColor(R.styleable.ProgressButton_android_textColor, onPrimaryColor)
                btnTextColor = makeContainedTextColor(attrButtonTextColor, colorOnSurface)
                buttonBackgroundTint = makeContainedBackgroundColor(attrButtonBackgroundTint, colorOnSurface)
                buttonRippleColor = makeContainedRippleColor(attrButtonTextColor)
            }
            else -> {
                val binding = FlatProgressButtonBinding.inflate(LayoutInflater.from(context), this)
                button = binding.fpbtnButton
                progressBar = binding.fpbtnProgressBar

                attrButtonTextColor = attributes.getColor(R.styleable.ProgressButton_android_textColor, primaryColor)
                btnTextColor = makeFlatTextColor(attrButtonTextColor, colorOnSurface)
                buttonBackgroundTint = makeFlatBackgroundColor(attrButtonBackgroundTint, resources)
                buttonRippleColor = makeFlatRippleColor(attrButtonTextColor)
            }
        }

        button.run {
            isEnabled = attributes.getBoolean(R.styleable.ProgressButton_android_enabled, true)
            text = attributes.getString(R.styleable.ProgressButton_android_text) ?: context.getString(R.string.pbtn__default_text)
            setTextColor(btnTextColor)
            backgroundTintList = buttonBackgroundTint
            rippleColor = buttonRippleColor

            if (buttonType == 0) {
                val defInnerMargin = resources.getDimensionPixelSize(R.dimen.btn__default_inner_margin)
                val innerMargin = attributes.getDimensionPixelSize(R.styleable.ProgressButton_innerMargin, defInnerMargin)
                (layoutParams as MarginLayoutParams).setMargins(innerMargin)
            }
        }

        // Progress bar initialization
        progressBar.run {
            val defPbrSize = resources.getDimensionPixelSize(R.dimen.pbr__default_size)
            val pbrSize = attributes.getDimensionPixelSize(R.styleable.ProgressButton_progressBarSize, defPbrSize)

            layoutParams.width = pbrSize
            layoutParams.height = pbrSize
            indeterminateTintList = attributes.getColorStateList(R.styleable.ProgressButton_android_indeterminateTint)
                ?: ColorStateList.valueOf(attrButtonTextColor)
        }

        ignoreByDelegate = attributes.getBoolean(R.styleable.ProgressButton_ignoreByDelegate, false)
        attributes.recycle()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        button.setOnClickListener(l)
    }

    override fun callOnClick(): Boolean {
        return button.callOnClick()
    }

    override fun isEnabled(): Boolean {
        return button.isEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        button.isEnabled = enabled
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            button.setTextColor(transparentColor)
            button.isClickable = false
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
            button.isClickable = true
            button.setTextColor(btnTextColor)
        }
    }
}