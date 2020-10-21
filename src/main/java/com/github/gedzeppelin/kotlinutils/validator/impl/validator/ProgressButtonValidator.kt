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

package com.github.gedzeppelin.kotlinutils.validator.impl.validator

import android.content.Context
import com.github.gedzeppelin.kotlinutils.validator.Validator
import com.github.gedzeppelin.kotlinutils.widget.ProgressButton

/**
 * Validator class that uses a [ProgressButton] as activator view.
 * @see Validator
 *
 * @property isLoading boolean flag that proxy the [ProgressButton.isLoading] variable.
 */
class ProgressButtonValidator(ctx: Context, pbtn: ProgressButton) : Validator<ProgressButton>(ctx, pbtn) {
    override var isLoading: Boolean
        get() = target.isLoading
        set(value) {
            target.isLoading = value
        }

    /**
     * @overrides hides the button's text and show a ProgressBar inside the button on a valid click.
     *
     * @see Validator.onTargetClick
     */
    override fun onTargetClick() {
        target.isLoading = true
        super.onTargetClick()
    }
}