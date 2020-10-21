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

package com.github.gedzeppelin.kotlinutils.validator.impl.validatable

import android.widget.EditText
import com.github.gedzeppelin.kotlinutils.validator.State
import com.github.gedzeppelin.kotlinutils.validator.ValidatorContext
import com.google.android.material.textfield.TextInputLayout


/**
 * An extension of [ValidatableEditText] which uses a TextInputEditText (a Google material implementation of
 * EditText), overrides the error display delegating it to another view, a TextInputLayout.
 *
 * @property callback validation block, must returns the validity of an editable object.
 * @property error the error to be displayed on validator error state.
 * @property value the text input view to be validated.
 * @property til the view where the error will be displayed.
 *
 * @see ValidatableEditText
 */
class ValidatableEditTextLayout<T : EditText, S : TextInputLayout>(
    ctx: ValidatorContext,
    error: CharSequence,
    private val til: S,
    value: T,
    callback: (String) -> Boolean,
    state: State
) : ValidatableEditText<T>(ctx, error, value, callback, state) {
    override fun onError(err: CharSequence) {
        til.error = err
    }

    override fun onSuccess() {
        til.error = null
    }
}