package com.github.ajsnarr98.linknotes.util.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.ajsnarr98.linknotes.R
import java.io.Serializable
import java.lang.IllegalStateException

/**
 * ConfirmationDialog represents a popup window for saying yes or cancel to a message.
 *
 * Construct with newInstance() method.
 */
class ConfirmationDialogFragment: DialogFragment() {

    companion object {

        private const val MESSAGE_KEY = "message"
        private const val POSITIVE_MESSAGE_KEY = "positiveButtonMessage"
        private const val ON_NEGATIVE_KEY = "onNegative"
        private const val ON_POSITIVE_KEY = "onPositive"

        fun newInstance(message: String,
                        onConfirm: () -> Unit,
                        onCancel: () -> Unit)
                : ConfirmationDialogFragment {

            val frag = ConfirmationDialogFragment()
            frag.arguments = Bundle().apply {
                putString(MESSAGE_KEY, message)
                putSerializable(ON_NEGATIVE_KEY, onCancel as Serializable)
                putSerializable(ON_POSITIVE_KEY, onConfirm as Serializable)
            }
            return frag
        }

        fun newInstance(message: String,
                        confirmButtonMessage: String,
                        onConfirm: () -> Unit,
                        onCancel: () -> Unit)
                : ConfirmationDialogFragment {

            val frag = ConfirmationDialogFragment()
            frag.arguments = Bundle().apply {
                putString(MESSAGE_KEY, message)
                putString(POSITIVE_MESSAGE_KEY, confirmButtonMessage)
                putSerializable(ON_NEGATIVE_KEY, onCancel as Serializable)
                putSerializable(ON_POSITIVE_KEY, onConfirm as Serializable)
            }
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val context: Context = activity ?: throw IllegalStateException("Could not access activity")

        val message = arguments?.getString(MESSAGE_KEY)
            ?: throw IllegalStateException("No message found for Confirmation Dialogue?")
        val onPositiveButtonClick: () -> Any = arguments?.getSerializable(ON_POSITIVE_KEY) as? (() -> Any)?
            ?: throw IllegalStateException("No confirm button callback found for Confirmation Dialogue?")
        val onNegativeButtonClick: () -> Any = arguments?.getSerializable(ON_NEGATIVE_KEY) as? (() -> Any)?
            ?: throw IllegalStateException("No cancel button callback found for Confirmation Dialogue?")

        val positiveButtonText: String = arguments?.getString(POSITIVE_MESSAGE_KEY)
            ?: context.getString(R.string.confirm)
        val negativeButtonText: String = context.getString(R.string.cancel)

        val alertDialogBuilder =  AlertDialog.Builder(context)

        alertDialogBuilder.apply {
            setTitle("")
            setMessage(message)
            setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveButtonClick()
                dialog?.dismiss()
            }
            setNegativeButton(negativeButtonText) { dialog, _ ->
                onNegativeButtonClick()
                dialog?.dismiss()
            }
        }

        return alertDialogBuilder.create()
    }
}
