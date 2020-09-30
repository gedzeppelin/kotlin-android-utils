@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val SUCCESSFUL_ACTION_FRAGMENT_RESULT_KEY = "kotlinutils.action.successfulAction"
const val ACTION_BUNDLE_KEY = "kotlinutils.action"
const val PAYLOAD_BUNDLE_KEY = "kotlinutils.successfulPayload"

const val CREATE_FRAGMENT_TAG = "kotlinutils.action.createFragment"
const val DELETE_FRAGMENT_TAG = "kotlinutils.action.deleteFragment"
const val ACTION_FRAGMENT_TAG = "kotlinutils.action.actionFragment"
const val ACTIVATE_FRAGMENT_TAG = "kotlinutils.action.activateFragment"
const val DEACTIVATE_FRAGMENT_TAG = "kotlinutils.action.deactivateFragment"
const val MODIFY_FRAGMENT_TAG = "kotlinutils.action.modifyFragment"

@Parcelize
enum class Action : Parcelable { CREATE, MODIFY, ACTIVATE, DEACTIVATE, DELETE }

val Action.flag: Boolean get() = this == Action.ACTIVATE

fun Action.asFlag(): Boolean {
    return this == Action.ACTIVATE
}

fun flag(action: Action): Boolean {
    return action == Action.ACTIVATE
}