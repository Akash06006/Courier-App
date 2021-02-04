package com.courierdriver.utils.broadcastReceiver

interface SmsListener {
    fun messageReceived(messageText: String?)
}