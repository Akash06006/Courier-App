package com.courierdriver.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WorkStatusBroadcastReceiver : BroadcastReceiver {
    private var notifyWorkStatus: NotifyWorkStatus? = null

    constructor()

    constructor(notifyWorkStatus: NotifyWorkStatus?) {
        this.notifyWorkStatus = notifyWorkStatus
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (notifyWorkStatus != null)
            notifyWorkStatus!!.refreshWorkStatusData()
    }
}

interface NotifyWorkStatus {
    fun refreshWorkStatusData()
}