package com.example.testtaskapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testtaskapplication.model.SMSModel
import com.example.testtaskapplication.repository.SMSRepository

class SMSViewModel (private val smsRepository: SMSRepository): ViewModel() {

    val data: LiveData<ArrayList<SMSModel>> = smsRepository.data

    fun getSmsList() {
        smsRepository.readSmsMessages()
    }

    fun addIncomingSms(sms: SMSModel) {
        smsRepository.addSms(sms)
    }

    class Factory(private val repository: SMSRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SMSViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SMSViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}