package com.example.testtaskapplication.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import com.example.testtaskapplication.model.SMSModel
import com.example.testtaskapplication.viewmodel.SMSViewModel


class SMSReceiver : BroadcastReceiver() {

    private var smsViewModel: SMSViewModel? = null

    fun setViewModel(viewModel: SMSViewModel) {
        smsViewModel = viewModel
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SMSReceiver", "Broadcast Received")

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle["pdus"] as? Array<*>
                if (!pdus.isNullOrEmpty()) {
                    for (pdu in pdus) {
                        val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                        val sender = smsMessage.displayOriginatingAddress
                        val messageBody = smsMessage.messageBody
                        val timeStamp = smsMessage.timestampMillis

                        Log.d("SMSReceiver", "SMS from: $sender, Message: $messageBody")

                        smsViewModel?.addIncomingSms(SMSModel(sender, messageBody, timeStamp))
                    }
                }
            }
        }
    }
}

