package com.example.testtaskapplication.view.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.testtaskapplication.R
import com.google.android.material.button.MaterialButton

class MessageDialog : DialogFragment() {

    private var listener: MessageDialogListener? = null

    interface MessageDialogListener {
        fun onMessageSent(message: String, senderPhoneNumber: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as MessageDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MessageDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dailog_message_layout, null)

        val editTextMessage = view.findViewById<EditText>(R.id.edit_message)
        val editTextPhoneNumber = view.findViewById<EditText>(R.id.edit_phone_number)
        val buttonSend = view.findViewById<MaterialButton>(R.id.button_send)

        builder.setView(view)

        val dialog = builder.create()

        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            if (message.isNotEmpty()) {
                listener?.onMessageSent(message, phoneNumber)
                dialog.dismiss()
            }
        }

        return dialog
    }
}
