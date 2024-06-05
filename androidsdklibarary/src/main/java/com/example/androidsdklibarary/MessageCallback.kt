package com.example.androidsdklibarary// MessageCollectorModule.kt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MessageCallback {
    fun onMessagesCollected(messages: List<String>)
}

class MessageCollectorModule(private val context: Context) {

    private var callback: MessageCallback? = null

    fun setCallback(callback: MessageCallback) {
        this.callback = callback
    }

    fun collectSMSMessages(): MutableList<String>? {
        val messages = mutableListOf<String>()

        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission not granted
            return null
        }

        // Query the SMS content provider to retrieve messages
        val uri = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            Telephony.Sms.DEFAULT_SORT_ORDER
        )

        // Iterate through the cursor to collect messages
        cursor?.use {
            while (it.moveToNext()) {
                val messageBody =
                    it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                messages.add(messageBody)
            }
        }

        // Notify the callback with collected messages
        callback?.onMessagesCollected(messages)

        return messages
    }
}
