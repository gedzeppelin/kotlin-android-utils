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

@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.gedzeppelin.kotlinutils.validator


enum class State { ASLEEP, ACTIVE, STOPPED }

/**
 * (ES) Interfaz que contiene la abstracción base para que un objeto sea Validable.
 * (EN) Interface that declares the base abstraction for an object to be Validatable.
 *
 * @property error (ES) mensaje que se muestra cuando [checkValidity] retorna falso.
 *   (EN) message to be displayed when [checkValidity] returns false.
 */
abstract class Validatable(
    val vContext: ValidatorContext,
    var error: CharSequence,
    state: State
) {
    var state: State = state
        set(value) {
            val state0 = field
            field = value
            if (field == State.ACTIVE) {
                if (state0 != State.ACTIVE) onStart()
                validate()
            } else {
                if (state0 == State.ACTIVE) onStop()
                onSuccess()
            }
        }

    var isValid: Boolean = false
        private set

    /**
     *
     */
    abstract fun checkValidity(): Boolean

    /**
     *
     */
    abstract fun onError(err: CharSequence = error)

    /**
     *
     */
    abstract fun onSuccess()

    /**
     *
     */
    fun validate(validity: Boolean): Boolean {
        if (state != State.STOPPED) {
            isValid = validity

            if (state == State.ACTIVE) onValidate(validity)
            vContext.validate()
        }

        return validity
    }

    /**
     * (ES) Realiza una validación, muestra (u oculta) el error y retorna el resultado de la validación.
     * Se asigna el valor de [checkValidity] al valor que se retorna.
     * (EN) Perform a validation, show (or hide) the error and return the validation result.
     * The value of [checkValidity] is assigned to the returned value.
     *
     * @return (ES) el resultado de la validación.
     *   (EN) the validation result.
     */
    fun validate(): Boolean = validate(checkValidity())


    /**
     *
     */
    fun refresh(): Boolean {
        if (state != State.STOPPED) {
            if (state == State.ACTIVE) onValidate(isValid)
            vContext.validate()
        }

        return isValid
    }

    /**
     * (ES) Muestra u oculta el texto de error en estado de invalidez. Esta función se llama una vez
     * por validación.
     * (EN) Displays or hides the error text on invalid state. This function is called once per
     * validation.
     *
     * @param isValid (ES) el error a mostrar, nulo en validación exitosa.
     *   (EN) the error to show, null when validation is successful.
     */
    protected open fun onValidate(isValid: Boolean) {
        if (isValid) onSuccess() else onError()
    }

    /**
     * (ES) Función que se invoca cada vez que el validador asociado es iniciado.
     * (EN) Function that is invoked every time the associated validator is started.
     */
    protected abstract fun onStart()

    /**
     * (ES) Función que se invoca cada vez que el validador asociado es detenido.
     * (EN) Function that is invoked every time the associated validator is stopped.
     */
    protected abstract fun onStop()
}

/**
 * (ES) Interfaz que contiene la abstracción base para que un objeto sea Validable.
 * (EN) Interface that declares the base abstraction for an object to be Validatable.
 *
 * @property T (ES) el tipo del [value] sobre el cuál se realizan validaciones.
 *   (EN) the type of the [value] to realize validate from.
 *
 * @property callback (ES) la función con la que validar el input.
 *   (EN) the function to validate the input with.
 * @property value (ES) el objeto que se valida.
 *   (EN) te object that is validated.
 */
abstract class ValidatableRequired<T : Any>(
    ctx: ValidatorContext,
    error: CharSequence,
    state: State
) : Validatable(ctx, error, state) {
    abstract val value: T
    abstract var callback: (T) -> Boolean
    override fun checkValidity(): Boolean = callback(value)
}

/**
 * (ES) Interfaz que contiene la abstracción base para que un objeto sea Validable.
 * (EN) Interface that declares the base abstraction for an object to be Validatable.
 *
 * @property T (ES) el tipo del [value] sobre el cuál se realizan validaciones.
 *   (EN) the type of the [value] to realize validate from.
 *
 * @property callback (ES) la función con la que validar el input.
 *   (EN) the function to validate the input with.
 * @property value (ES) el objeto que se valida.
 *   (EN) te object that is validated.
 */
abstract class ValidatableNullable<T : Any>(
    ctx: ValidatorContext,
    error: CharSequence,
    state: State
) : Validatable(ctx, error, state) {
    abstract val value: T?
    abstract var callback: (T?) -> Boolean
    override fun checkValidity(): Boolean = callback(value)
}