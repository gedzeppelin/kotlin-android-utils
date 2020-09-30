package com.github.gedzeppelin.kotlinutils.validator.impl1

import com.github.gedzeppelin.kotlinutils.validator.State
import com.github.gedzeppelin.kotlinutils.validator.ValidatableContext
import com.github.gedzeppelin.kotlinutils.validator.ValidatableNullable
import com.github.gedzeppelin.kotlinutils.validator.ValidatableRequired

class ValidatableField<T : Any>(
    ctx: ValidatableContext,
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
        validatableContext.errorCache.add(error)
    }

    override fun onSuccess() {
        validatableContext.errorCache.remove(error)
    }

    override fun onStart() = Unit

    override fun onStop() = onSuccess()
}

class ValidatableRequiredField<T : Any>(
    ctx: ValidatableContext,
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
        validatableContext.errorCache.add(error)
    }

    override fun onSuccess() {
        validatableContext.errorCache.remove(error)
    }

    override fun onStart() = Unit

    override fun onStop() = onSuccess()
}