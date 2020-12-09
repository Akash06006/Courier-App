package  com.courierdriver.utils.offlineSyncing

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.courierdriver.utils.service.NetworkChangeCallback

class NetworkChangeReceiver : BroadcastReceiver {
    private var callback: NetworkChangeCallback? = null

    constructor()

    constructor(callback: NetworkChangeCallback?) {
        this.callback = callback
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (isOnline(context)) {
               // UtilsFunctions.showToastSuccess("App is Online")
                callback!!.onNetworkChanged(true)
                //OfflineSyncing.userDataSyncing()
            } else {
                // UtilsFunctions.showToastSuccess("App is Offline")
                callback!!.onNetworkChanged(false)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }

    }
}