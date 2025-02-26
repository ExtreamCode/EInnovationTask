package com.example.testtaskapplication.view

import android.app.PendingIntent
import android.content.Intent
import com.example.testtaskapplication.Utils.SMSReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.testtaskapplication.R
import com.example.testtaskapplication.Utils.SMSPermissionUtil
import com.example.testtaskapplication.constant.TaskConstant
import com.example.testtaskapplication.databinding.ActivityMainBinding
import com.example.testtaskapplication.model.SMSModel
import com.example.testtaskapplication.repository.SMSRepository
import com.example.testtaskapplication.view.components.MessageDialog
import com.example.testtaskapplication.viewmodel.SMSViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pikpart.businessmanager.adapter.FNRecyclerAdaptor


class MainActivity : AppCompatActivity(), MessageDialog.MessageDialogListener {


    private lateinit var vModel : SMSViewModel
    private lateinit var  smsReciever : SMSReceiver
    private lateinit var binding: ActivityMainBinding
    private lateinit var smsAdapter : FNRecyclerAdaptor<SMSModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().length >= 5) {
                    smsAdapter.filter.filter(s.toString())
                }
            }
        })


        vModel = ViewModelProvider(this, SMSViewModel.Factory(SMSRepository(this)))[SMSViewModel::class.java]
        smsReciever = SMSReceiver()
        smsReciever.setViewModel(vModel)
        val permission = SMSPermissionUtil(this)
        if (!permission.hasSmsPermissions()) {
            permission.requestSmsPermissions()
            val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            registerReceiver(smsReciever, filter)
        } else {
            getSmsList()
        }

        binding.mbSend.setOnClickListener {
                val dialog = MessageDialog()
                dialog.show(supportFragmentManager, "MessageDialog")
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == TaskConstant.SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getSmsList()

                val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
                registerReceiver(smsReciever, filter)

            } else {
                Toast.makeText(this, "Permission Denied. Cannot access SMS.", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getSmsList() {
        vModel.getSmsList()
        vModel.data.observe(this) { response ->
            smsAdapter.updateItems(response)
        }
        loadSMS()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReciever, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsReciever)
    }

    private fun loadSMS(){
        smsAdapter = FNRecyclerAdaptor(
            arrayListOf(),
            object : FNRecyclerAdaptor.SGRecyclerRowCreator<SMSModel> {
                override fun createRecyclerRow(
                    convertView: View,
                    rowObject: SMSModel,
                    position: Int
                ) {
                    val sender: TextView = convertView.findViewById(R.id.tv_user_id)
                    sender.text = rowObject.sender

                    val message: TextView = convertView.findViewById(R.id.tv_message)
                    message.text = rowObject.message

                    val time: TextView = convertView.findViewById(R.id.tv_time)
                    time.text = rowObject.timestamp.toString()
                }
            },
            R.layout.row_sms_list_item
        )
        binding.rvSms.adapter = smsAdapter
    }

    override fun onMessageSent(message: String, senderPhoneNumber : String) {
        val sentIntent = Intent("SMS_SENT")
        val deliveredIntent = Intent("SMS_DELIVERED")

        try {
            val sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE)
            val deliveredPI = PendingIntent.getBroadcast(this, 0, deliveredIntent, PendingIntent.FLAG_IMMUTABLE)
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(senderPhoneNumber, null, message, sentPI, deliveredPI)
            Toast.makeText(this, "SMS Sent Successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "SMS Failed to Send!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        Toast.makeText(this, "Message Sent: $message", Toast.LENGTH_SHORT).show()
    }
}