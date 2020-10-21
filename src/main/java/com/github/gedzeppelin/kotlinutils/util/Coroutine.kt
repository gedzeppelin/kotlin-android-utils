package com.github.gedzeppelin.kotlinutils.util

import android.content.res.Resources
import android.os.Parcelable
import androidx.core.app.ComponentActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.github.gedzeppelin.kotlinutils.*
import com.github.gedzeppelin.kotlinutils.GlobalToast.Companion.longToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

fun Resources.getActionMessages(action: Action): Array<out String> = when (action) {
    Action.CREATE -> getStringArray(R.array.action_create)
    Action.MODIFY -> getStringArray(R.array.action_modify)
    Action.ACTIVATE -> getStringArray(R.array.action_activate)
    Action.DEACTIVATE -> getStringArray(R.array.action_deactivate)
    Action.DELETE -> getStringArray(R.array.action_delete)
}

fun <T : DialogFragment, S> T.actionLaunch(
    action: Action,
    prop: KProperty1<S, String>,
    onSuccess: ((payload: S) -> Unit)? = {
        val bundle = bundleWith {
            putParcelable(ACTION_BUNDLE_KEY, action)
            putParcelable(PAYLOAD_BUNDLE_KEY, it)
        }
        setFragmentResult(SUCCESSFUL_ACTION_FRAGMENT_RESULT_KEY, bundle)
    },
    onError: ((error: Response.Error) -> Unit)? = null,
    onComplete: ((response: Response<S>) -> Unit)? = { dismiss() },
    block: suspend () -> Response<S>
): Job where S : Any, S : Parcelable = lifecycleScope.launch {
    val actionMessages = resources.getActionMessages(action)
    val result = block()
    val message = when (result) {
        is Response.Success -> {
            onSuccess?.invoke(result.payload)
            getString(R.string.sheet_action__on_success, actionMessages[0], prop.get(result.payload))
        }
        is Response.Error -> {
            onError?.invoke(result)
            getString(R.string.sheet_action__on_error, actionMessages[1])
        }
    }
    onComplete?.invoke(result)
    val stylizedMessage = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
    longToast(stylizedMessage)
}

fun <T : ComponentActivity, S : Any> T.actionLaunch(
    action: Action,
    prop: KProperty1<S, String>,
    block: suspend () -> Response<S>,
    onSuccess: ((payload: S) -> Unit)? = null,
    onError: ((error: Response.Error) -> Unit)? = null,
    onComplete: ((response: Response<S>) -> Unit)? = null
): Job = lifecycleScope.launch {
    val actionMessages = resources.getActionMessages(action)
    val result = block()
    val message = when (result) {
        is Response.Success -> {
            onSuccess?.invoke(result.payload)
            getString(R.string.sheet_action__on_success, actionMessages[0], prop.get(result.payload))
        }
        is Response.Error -> {
            onError?.invoke(result)
            getString(R.string.sheet_action__on_error, actionMessages[1])
        }
    }
    onComplete?.invoke(result)
    val stylizedMessage = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
    longToast(stylizedMessage)
}

fun <T : ComponentActivity, S : Any> T.actionLaunch(
    action: Action,
    displayName: (payload: S) -> String,
    block: suspend () -> Response<S>,
    onSuccess: ((payload: S) -> Unit)? = null,
    onError: ((error: Response.Error) -> Unit)? = null,
    onComplete: ((response: Response<S>) -> Unit)? = null
): Job = lifecycleScope.launch {
    val actionMessages = resources.getActionMessages(action)
    val result = block()
    val message = when (result) {
        is Response.Success -> {
            onSuccess?.invoke(result.payload)
            getString(R.string.sheet_action__on_success, actionMessages[0], displayName(result.payload))
        }
        is Response.Error -> {
            onError?.invoke(result)
            getString(R.string.sheet_action__on_error, actionMessages[1])
        }
    }
    onComplete?.invoke(result)
    val stylizedMessage = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
    longToast(stylizedMessage)
}