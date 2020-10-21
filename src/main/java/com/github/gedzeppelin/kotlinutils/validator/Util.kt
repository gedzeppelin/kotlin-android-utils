@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.validator

import android.content.Context
import android.text.InputType
import android.widget.EditText
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.validator.impl.validatable.ValidatableEditText
import com.github.gedzeppelin.kotlinutils.validator.impl.validator.ProgressButtonValidator
import com.github.gedzeppelin.kotlinutils.widget.ProgressButton
import kotlin.reflect.KProperty0

typealias PbtnValidator = ProgressButtonValidator
typealias VleEditText = ValidatableEditText<out EditText>

/**
 * (ES) Consigue un String de error adecuado basado en el tipo de input de [editText].
 * (EN) Retrieves an adequate error string based on the input type of [editText].
 *
 * @param editText (ES) el input view de texto del cual se conseguirá el error.
 *   (EN) the text input view to retrieve an error based on its input type.
 *
 * @return (ES) un String de error adecuado para el tipo de input de [editText].
 *   (EN) an adequate error string for the input type of [editText].
 */
fun <T : EditText> getDefaultError(context: Context, editText: T): String = when (editText.inputType) {
    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, 33 -> context.getString(R.string.til_error_email)
    InputType.TYPE_TEXT_VARIATION_PERSON_NAME, 97 -> context.getString(R.string.til_error_name)
    InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS -> context.getString(R.string.til_error_address)
    InputType.TYPE_CLASS_PHONE -> context.getString(R.string.til_error_phone)
    InputType.TYPE_TEXT_VARIATION_PASSWORD, 129 -> context.getString(R.string.til_error_password)
    else -> context.getString(R.string.til_error_default)
}

/**
 *
 */
fun <S : EditText> Validator<*>.addPasswordEditText(
    prop0: KProperty0<S>,
    prop1: KProperty0<S>,
    state: State = State.ASLEEP,
    callback: (String) -> Boolean
): Pair<VleEditText, VleEditText> {
    val editText0 = prop0.get()
    val editText1 = prop1.get()

    val bothError = aContext.getString(R.string.til_error__password_repeat)
    val error0 = getDefaultError(aContext, editText0)
    val error1 = getDefaultError(aContext, editText1)

    lateinit var validatable0: VleEditText
    lateinit var validatable1: VleEditText

    val callback0: (String) -> Boolean = {
        val other = validatable1.value.text.toString()

        if (callback(it)) {
            if (it == other) {
                validatable1.validate(true)
                true
            } else {
                validatable0.error = bothError
                if (validatable1.isValid) validatable1.onError(bothError)
                else validatable1.refresh()
                false
            }
        } else {
            validatable0.error = error0
            validatable1.validate()
            false
        }
    }

    val callback1: (String) -> Boolean = {
        val other = validatable0.value.text.toString()

        if (callback(it)) {
            if (it == other) {
                validatable0.validate(true)
                true
            } else {
                validatable1.error = bothError
                if (validatable0.isValid) validatable0.onError(bothError)
                else validatable0.refresh()
                false
            }
        } else {
            validatable1.error = error1
            validatable0.refresh()
            false
        }
    }

    validatable0 = addEditText(prop0.name, error0, editText0, callback0, state)
    validatable1 = addEditText(prop1.name, error1, editText1, callback1, state)

    return validatable0 to validatable1
}

/**
 *
 */
fun String.regex(pattern: String): Boolean = Regex(pattern).matches(this)

/**
 *
 */
fun ProgressButton.attachValidator(block: PbtnValidator.() -> Unit) =
    PbtnValidator(this.context, this).apply(block)

/**
 *
 */
fun ProgressButton.attachValidator(ctx: Context, block: PbtnValidator.() -> Unit) =
    PbtnValidator(ctx, this).apply(block)

