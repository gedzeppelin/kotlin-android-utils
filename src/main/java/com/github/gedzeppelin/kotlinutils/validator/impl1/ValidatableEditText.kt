/**
 * Copyright 2020 Gedy Palomino
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.gedzeppelin.kotlinutils.validator.impl1

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.github.gedzeppelin.kotlinutils.validator.State
import com.github.gedzeppelin.kotlinutils.validator.ValidatableContext
import com.github.gedzeppelin.kotlinutils.validator.ValidatableRequired

/**
 * (ES) Clase validable vinculada a una View, con [isValid] definido por una función. Usa como
 * parámetro para la función de validación un String. El error se muestra en el texto de ayuda de
 * error sobre un [EditText].
 * (EN) Validatable class attached to a View, [isValid] is defined by a function. Validation
 * function uses a String as a parameter. Error is displayed in a [EditText] error helper text.
 */
open class ValidatableEditText<T : EditText>(
    ctx: ValidatableContext,
    error: CharSequence,
    override var value: T,
    callback: (String) -> Boolean,
    state: State
) : ValidatableRequired<T>(ctx, error, state) {
    override var callback: (T) -> Boolean = { callback(it.text.toString()) }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validate()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }


    final override fun onStart() {
        value.addTextChangedListener(textWatcher)
    }

    final override fun onStop() {
        value.removeTextChangedListener(textWatcher)
    }

    override fun onError(err: CharSequence) {
        value.error = err
    }

    override fun onSuccess() {
        value.error = null
    }
}
