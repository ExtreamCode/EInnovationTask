package com.example.testtaskapplication.model

import java.util.Locale



data class SMSModel(val sender: String, val message: String, val timestamp: Long) {
    override fun toString(): String = sender
    override fun equals(other: Any?): Boolean {
        if (other is String) {
            val str: String = other.toString().lowercase(Locale.getDefault())
            return (this.sender.lowercase(Locale.getDefault())
                .contains(str.trim()))
        }
        return true
    }

}
