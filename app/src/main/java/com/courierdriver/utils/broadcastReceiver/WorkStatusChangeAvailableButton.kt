package com.courierdriver.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WorkStatusChangeAvailableButton : BroadcastReceiver {
    private var notifyWorkStatus: NotifyWorkStatusButtons? = null

    constructor()

    constructor(notifyWorkStatus: NotifyWorkStatusButtons?) {
        this.notifyWorkStatus = notifyWorkStatus
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (notifyWorkStatus != null)
            notifyWorkStatus!!.refreshWorkStatusData()
    }
}

interface NotifyWorkStatusButtons {
    fun refreshWorkStatusData()
}