package com.example.testtaskapplication.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.example.testtaskapplication.model.SMSModel


object SMSsUtil {

    fun getAllSms(context: Context, filterSender: String = ""): ArrayList<SMSModel> {
        val smsList = mutableListOf<SMSModel>().let { ArrayList(it) }
        val uri: Uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "body", "date")

        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, "date DESC")

        cursor?.use {
            val senderIndex = it.getColumnIndexOrThrow("address")
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val dateIndex = it.getColumnIndexOrThrow("date")

            while (it.moveToNext()) {
                val sender = it.getString(senderIndex)
                val message = it.getString(bodyIndex)
                val timestamp = it.getLong(dateIndex)

                // Filter by sender ID if provided
                if (filterSender.isEmpty() || sender == filterSender) {
                    smsList.add(SMSModel(sender, message, timestamp))
                }
            }
        }
        return smsList
    }
}
