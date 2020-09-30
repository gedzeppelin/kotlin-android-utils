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

package com.github.gedzeppelin.kotlinutils.validator

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.github.gedzeppelin.kotlinutils.GlobalToast.Companion.longToast
import com.github.gedzeppelin.kotlinutils.validator.impl1.ValidatableEditText
import com.github.gedzeppelin.kotlinutils.validator.impl1.ValidatableEditTextLayout
import com.github.gedzeppelin.kotlinutils.validator.impl1.ValidatableField
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.reflect.KProperty0

/**
 * (ES) Validador Base, clase que se puede extender para soportar validadores customizados.
 * (EN) Base validator class, all validator classes must inherit from this.
 *
 * @property T
 *
 * @property target (ES) la View a la que está vinculada el Validador. Solo está habilitado
 *   cuando todos los [ValidatableRequired] en [vMap] son válidos.
 *   (EN) the View to which the validator is rooted. It's only enabled when all [ValidatableRequired]
 *   objects in [vMap] are valid.
 * @property onTargetClickListener (ES) la función que se ejecuta en un click válido sobre [target].
 *   (EN) the function that is executed on [target] valid click.
 * @property context (ES) el contexto en el que esta clase es instanciada.
 *   (EN) the context which the class is instantiated with.
 * @property hasStarted (ES) falso hasta que se realiza el primer click sobre [target].
 *   (EN) false until the first click on activatorView.
 * @property isEnabled (ES) realiza un proxy con la variable isEnabled de [target].
 *   (EN) proxys [target] isEnabled variable.
 * @property vMap (ES) Set de objetos [ValidatableRequired]. Maneja y almacena las
 *   validaciones individuales.
 *   (EN) Set of [ValidatableRequired] items. Handles and stores individual validations.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class Validator<T : View>(
    override val context: Context,
    val target: T
) : ValidatableContext {
    override val errorCache = mutableSetOf<CharSequence>()

    override fun validate() {
        if (hasStarted) {
            val v0 = vMap.filterValues { it.state == State.ACTIVE && !it.isValid }
            isEnabled = v0.isEmpty()
        }
    }

    private var onTargetClickListener: ((T) -> Unit)? = null
    private val vMap = mutableMapOf<String, Validatable>()

    // TODO find a way to remove the necessity of using the stop cache.
    private val vStopCache = mutableSetOf<String>()

    private var hasStarted: Boolean = false
    abstract var isLoading: Boolean
    var isEnabled: Boolean
        get() = target.isEnabled
        private set(value) {
            target.isEnabled = value
        }

    /**
     * (ES) Agrega un objeto validable a [vMap].
     * (EN) Adds a validatable object to [vMap].
     *
     * @return (ES) true si el elemento ha sido añadido, falso si el elemento ya existe en el set.
     *   (EN) true if the element has been added, false if the element is already contained in the set.
     */
    fun contains(key: String) = vMap.containsKey(key)

    fun contains(prop: KProperty0<*>) = contains(prop.name)

    @Suppress("UNCHECKED_CAST")
    fun <S : Validatable> add(key: String, validatable: S): S {
        val current = vMap[key] as? S
        if (current != null) return current

        vMap[key] = validatable

        if (vStopCache.remove(key)) validatable.state = State.STOPPED

        return validatable
    }

    fun <S : Validatable> add(prop: KProperty0<S>) = add(prop.name, prop.get())

    fun start(key: String): Boolean {
        val validator = vMap[key]
        if (validator != null) {
            validator.state = if (hasStarted) State.ACTIVE else State.ASLEEP
            return true
        }
        return false
    }

    fun start(prop: KProperty0<*>) = start(prop.name)

    fun stop(key: String): Boolean {
        val validator = vMap[key]
        return if (validator != null) {
            validator.state = State.STOPPED
            true
        } else {
            vStopCache.add(key)
            false
        }
    }

    fun stop(prop: KProperty0<*>) = stop(prop.name)

    fun remove(key: String): Boolean {
        val validator = vMap[key]
        return if (validator != null) vMap.remove(key) != null else false
    }

    fun remove(prop: KProperty0<*>) = remove(prop.name)

    /**
     * Internal helper function used in all overload functions that adds a validator item to
     * [Validator.vMap], it accepts as input view an EditText or an extension of it.
     *
     * @param error the error to be displayed on error state.
     * @param inputView the view to be validated, must be extends EditText.
     * @param callback validation block, must returns the validity of an editable object.
     */
    fun <S : EditText> addEditText(
        key: String,
        error: Int,
        inputView: S,
        callback: (String) -> Boolean,
        state: State
    ): ValidatableEditText<out EditText> {
        val errorString = context.getString(error)
        // Try to get the parent TextInputLayout.
        val til = (inputView.parent as? FrameLayout)?.parent as? TextInputLayout

        // Get the validator instance.
        val validator = if (inputView is TextInputEditText && til != null) {
            ValidatableEditTextLayout(this, errorString, til, inputView, callback, state)
        } else {
            ValidatableEditText(this, errorString, inputView, callback, state)
        }

        // Add the validator to list.
        return add(key, validator)
    }

    /**
     *
     */
    fun <S : EditText> addEditText(
        key: String,
        error: String,
        inputView: S,
        callback: (String) -> Boolean,
        state: State
    ): VleEditText {
        // Try to get the parent TextInputLayout.
        val parentTil = (inputView.parent as? FrameLayout)?.parent as? TextInputLayout

        // Get the validator instance.
        val validator = if (parentTil != null) {
            ValidatableEditTextLayout(this, error, parentTil, inputView, callback, state)
        } else {
            ValidatableEditText(this, error, inputView, callback, state)
        }

        // Add the validator to list.
        return add(key, validator)
    }

    /**
     * Adds a lazy [ValidatableEditText] to. The error message to be displayed on
     * its view's invalid state is a string resource.
     *
     * @see addEditText
     *
     * @param prop the view to be validated, must be inherits TextInputEditText.
     * @param error the error to be displayed on error state.
     * @param callback validation block, returns the validation state of the validator view.
     */
    fun <T : EditText> addEditText(
        prop: KProperty0<T>,
        error: Int,
        state: State = State.ASLEEP,
        callback: (String) -> Boolean = { it.trim().isNotEmpty() }
    ) = addEditText(prop.name, error, prop.get(), callback, state)

    /**
     * @overload adds lazy validator with error message as String object.
     * @see addEditText
     */
    fun <T : EditText> addEditText(
        prop: KProperty0<T>,
        error: String,
        state: State = State.ASLEEP,
        callback: (String) -> Boolean = { it.trim().isNotEmpty() }
    ) = addEditText(prop.name, error, prop.get(), callback, state)

    /**
     * @overload adds lazy validator with a default error message.
     * @see addEditText
     */
    fun <T : EditText> addEditText(
        prop: KProperty0<T>,
        state: State = State.ASLEEP,
        callback: (String) -> Boolean = { it.trim().isNotEmpty() }
    ): VleEditText {
        val editText = prop.get()
        val error = getDefaultError(context, editText)
        return addEditText(prop.name, error, editText, callback, state)
    }

    /**
     * (EXPERIMENTAL) [ValidatableField] object list:
     * @see addField to add custom validations.
     * @see getField to get a custom validation variable.
     * @see setField to set a custom validation variable.
     */

    fun <S : Any> addField(
        key: String,
        error: Int,
        initial: S? = null,
        state: State = State.ASLEEP,
        callback: (S?) -> Boolean = { it != null }
    ): ValidatableField<S> {
        val errorStr = context.getString(error)
        val validatable = ValidatableField(this, errorStr, initial, callback, state)
        return add(key, validatable)
    }

    fun <S : Any> addField(
        key: String,
        error: String,
        initial: S? = null,
        state: State = State.ASLEEP,
        callback: (S?) -> Boolean = { it != null }
    ): ValidatableField<S> {
        val validatable = ValidatableField(this, error, initial, callback, state)
        return add(key, validatable)
    }


    /**
     *
     */
    @Suppress("UNCHECKED_CAST")
    fun <S : Any> getField(
        key: String
    ): S? {
        val validatable = vMap[key] as? ValidatableField<S>
        if (validatable != null) return validatable.value
        throw IllegalStateException("Validatable field is not added or is not a validatable field")
    }

    /**
     *
     */
    @Suppress("UNCHECKED_CAST")
    fun <S : Any> setField(
        key: String,
        value: S?
    ): Boolean {
        val validatable = vMap[key] as? ValidatableField<S>

        if (validatable != null) {
            validatable.value = value
            return true
        }

        return false
    }


    /**
     * Sets on click logic to the activator view.
     * - If validation has started and the view is enabled (view is only enabled when global
     *   valid state is true) invokes [onTargetClickListener] with [onTargetClickListener].
     * - If validation has not started yet [hasStarted] is setted to true, lazy validators
     *   are started and all validators are (re)validated, then global valid state is checked and
     *   assigned to the activator view's enabled state, if true also invokes [onTargetClickListener].
     */
    open fun startLazy(block: (T) -> Unit) {
        /* Initialize lateinit property. */
        onTargetClickListener = block
        /* Set activator view logic. */
        target.setOnClickListener {
            // If already started and the activator view is enabled the activation block must be invoked.
            if (hasStarted) {
                onTargetClick()
            } else {
                vMap.values.forEach {
                    // Start lazy validators.
                    if (it.state == State.ASLEEP) it.state = State.ACTIVE
                }

                if (errorCache.isNotEmpty()) {
                    val snackbarError = errorCache.fold("Error:") { acc, s -> "$acc\n- $s" }
                    context.longToast(snackbarError)
                }

                // Set the validator as started.
                hasStarted = true

                /* Change enabled state of activator view. */
                validate()

                /* If the validator is valid then the activation block must be invoked. */
                if (isEnabled) onTargetClick()
            }
        }
    }

    /**
     * (ES)
     * (EN) This function is invoked when a valid click is preformed on the activator view. Any
     * additional logic related with a valid click can be added when overriding this method.
     */
    open fun onTargetClick() {
        onTargetClickListener?.invoke(target)
    }
}