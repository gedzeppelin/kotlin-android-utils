@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.validator.impl.validatable

import com.github.gedzeppelin.kotlinutils.validator.State
import com.github.gedzeppelin.kotlinutils.validator.ValidatableNullable
import com.github.gedzeppelin.kotlinutils.validator.ValidatableRequired
import com.github.gedzeppelin.kotlinutils.validator.ValidatorContext

class ValidatableField<T : Any>(
    ctx: ValidatorContext,
    error: CharSequence,
    initial: T?,
    override var callback: (T?) -> Boolean,
    state: State
) : ValidatableNullable<T>(ctx, error, state) {
    override var value: T? = initial
        set(value) {
            field = value
            validate()
        }

    override fun onError(err: CharSequence) {
        vContext.errorCache.add(error)
    }

    override fun onSuccess() {
        vContext.errorCache.remove(error)
    }

    override fun onStart() = Unit

    override fun onStop() = onSuccess()
}

class ValidatableRequiredField<T : Any>(
    ctx: ValidatorContext,
    error: CharSequence,
    initial: T,
    override var callback: (T) -> Boolean,
    state: State
) : ValidatableRequired<T>(ctx, error, state) {
    override var value: T = initial
        set(value) {
            field = value
            validate()
        }

    override fun onError(err: CharSequence) {
        vContext.errorCache.add(error)
    }

    override fun onSuccess() {
        vContext.errorCache.remove(error)
    }

    override fun onStart() = Unit

    override fun onStop() = onSuccess()
}