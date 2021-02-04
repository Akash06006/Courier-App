package com.courierdriver.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {
    var b: Boolean? = null
    var abcd: String? = null
    var xyz: String? = null
    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.extras
        val pdus = data["pdus"] as Array<Any>
        for (i in pdus.indices) {
            val smsMessage =
                SmsMessage.createFromPdu(pdus[i] as ByteArray)
            val sender = smsMessage.displayOriginatingAddress
            // b=sender.endsWith("WNRCRP");  //Just to fetch otp sent from WNRCRP
            val messageBody = smsMessage.messageBody
            abcd = messageBody.replace("[^0-9]".toRegex(), "") // here abcd contains otp
            //Pass on the text to our listener.
            if (b == true) {
                mListener!!.messageReceived(abcd) // attach value to interface
            } else {
            }
        }
    }

    companion object {
        private var mListener: SmsListener? = null
        fun bindListener(listener: SmsListener?) {
            mListener = listener
        }
    }
}