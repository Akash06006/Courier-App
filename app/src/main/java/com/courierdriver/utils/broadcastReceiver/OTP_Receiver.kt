package com.courierdriver.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import com.courierdriver.application.MyApplication
import com.goodiebag.pinview.Pinview

class OTP_Receiver : BroadcastReceiver() {
    fun setEditText(editText: Pinview?) {
        Companion.editText = editText
    }

    // OnReceive will keep trace when sms is been received in mobile
    override fun onReceive(context: Context, intent: Intent) {
        //message will be holding complete sms that is received
        try {
            val messages =
                Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages != null && messages.isNotEmpty()) {
                for (sms in messages) {
                    val msg = sms.messageBody
                    // here we are spliting the sms using " : " symbol
                    val n = 6
                    //  val otp = msg.split(": ".toRegex()).toTypedArray()[1]
                    /*Toast.makeText(MyApplication.instance, msg.substring(0, n), Toast.LENGTH_LONG)
                        .show()*/
                    editText!!.value = msg.substring(0, n)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private var editText: Pinview? = null
    }
}