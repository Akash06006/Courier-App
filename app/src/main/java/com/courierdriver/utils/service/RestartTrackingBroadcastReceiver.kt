package com.courierdriver.utils.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log


class RestartTrackingBroadcastReceiver : BroadcastReceiver {
    private var notifyRestartTrackingBroadcast: NotiFyRestartTrackingReceiver? = null

    constructor()

    constructor(notifyRestartTrackingBroadcast: NotiFyRestartTrackingReceiver?) {
        this.notifyRestartTrackingBroadcast = notifyRestartTrackingBroadcast
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("Broadcast Listened", "Service tried to stop")
        //  Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()

        if (notifyRestartTrackingBroadcast != null)
            notifyRestartTrackingBroadcast!!.refreshRestartTrackingData()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, TrackingService::class.java))
        } else {
            context.startService(Intent(context, TrackingService::class.java))
        }
    }
}

interface NotiFyRestartTrackingReceiver {
    fun refreshRestartTrackingData()
}