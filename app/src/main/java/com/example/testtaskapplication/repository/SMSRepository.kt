package com.example.testtaskapplication.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testtaskapplication.model.SMSModel
import com.example.testtaskapplication.utils.SMSsUtil

class SMSRepository (private val context: Context){

    private val _data = MutableLiveData<ArrayList<SMSModel>>()
    val data: LiveData<ArrayList<SMSModel>> = _data

     fun readSmsMessages() {
        val smsList = SMSsUtil.getAllSms(context)
        if (smsList != null) {
            _data.postValue(smsList)
        }
    }

    fun addSms(newSms: SMSModel) {
        val currentList = _data.value.orEmpty().toMutableList().let { ArrayList(it) }
        currentList.add(0, newSms)
        _data.postValue(currentList)
    }
}