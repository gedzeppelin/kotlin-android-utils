package com.github.gedzeppelin.kotlinutils.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.github.gedzeppelin.kotlinutils.*
import com.github.gedzeppelin.kotlinutils.GlobalSnackbar.Companion.longSnackbar
import com.github.gedzeppelin.kotlinutils.databinding.DialogActionBinding
import com.github.gedzeppelin.kotlinutils.util.bundleWith
import com.github.gedzeppelin.kotlinutils.util.withArgs
import kotlinx.coroutines.launch
import java.io.Serializable
import kotlin.reflect.KProperty1

class ActionDialog<T> : DialogFragment() where T : Any, T : Parcelable {
    companion object {
        private const val SUSPEND_CALLBACK_BUNDLE_KEY = "suspendFunction"
        private const val NAME_PROPERTY_BUNDLE_KEY = "nameProperty"

        @JvmStatic
        fun <T> newInstance(
            action: Action,
            suspendBlock: suspend (p: T, t: Action) -> Boolean,
            payload: T,
            nameProperty: KProperty1<T, String>
        ) where T : Any, T : Parcelable = ActionDialog<T>().withArgs {
            putParcelable(ACTION_BUNDLE_KEY, action)
            putParcelable(PAYLOAD_BUNDLE_KEY, payload)
            putSerializable(SUSPEND_CALLBACK_BUNDLE_KEY, suspendBlock as Serializable)
            putSerializable(NAME_PROPERTY_BUNDLE_KEY, nameProperty as Serializable)
        }
    }

    private var _binding: DialogActionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val action = requireArguments().getParcelable<Action>(ACTION_BUNDLE_KEY)
        val suspendBlock = requireArguments().getSerializable(SUSPEND_CALLBACK_BUNDLE_KEY)
            as? suspend (p: T, t: Action) -> Boolean
        val payload = requireArguments().getParcelable<T>(PAYLOAD_BUNDLE_KEY)
        val nameProperty = requireArguments().getSerializable(NAME_PROPERTY_BUNDLE_KEY)
            as? KProperty1<T, String>

        if (action != null && payload != null && suspendBlock != null && nameProperty != null) {
            val actionMessage = when (action) {
                Action.CREATE -> getString(R.string.action_create)
                Action.MODIFY -> getString(R.string.action_modify)
                Action.ACTIVATE -> getString(R.string.action_activate)
                Action.DEACTIVATE -> getString(R.string.action_deactivate)
                Action.DELETE -> getString(R.string.action_delete)
            }

            val name = nameProperty.get(payload)

            val confirmMessage = getString(R.string.action__restore_delete__confirmation, actionMessage, name)
            val styledMessage = HtmlCompat.fromHtml(confirmMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)

            binding.tvwMessage.text = styledMessage

            binding.pbtnYes.setOnClickListener {
                isCancelable = false
                binding.btnNo.isEnabled = false
                binding.pbtnYes.isLoading = true

                lifecycleScope.launch {
                    if (suspendBlock(payload, action)) {
                        val successAction = when (action) {
                            Action.CREATE -> getString(R.string.action_create_success)
                            Action.MODIFY -> getString(R.string.action_modify_success)
                            Action.ACTIVATE -> getString(R.string.action_activate_success)
                            Action.DEACTIVATE -> getString(R.string.action_deactivate_success)
                            Action.DELETE -> getString(R.string.action_delete_success)
                        }
                        val successMessage = getString(R.string.action__restore_delete__on_success, successAction, name)

                        setFragmentResult(SUCCESSFUL_ACTION_FRAGMENT_RESULT_KEY, bundleWith {
                            putParcelable(ACTION_BUNDLE_KEY, action)
                            putParcelable(PAYLOAD_BUNDLE_KEY, payload)
                        })
                        dismiss()
                        requireActivity().longSnackbar(successMessage)
                    } else {
                        isCancelable = true
                        binding.btnNo.isEnabled = true
                        binding.pbtnYes.isLoading = false

                        val errorMessage = getString(R.string.action__restore_delete__on_error, action, name)

                        dismiss()
                        requireActivity().longSnackbar(errorMessage)
                    }
                }
            }
            binding.btnNo.setOnClickListener { dismiss() }
        } else {
            dismiss()
            requireActivity().longSnackbar(R.string.toast_instantiation_error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}