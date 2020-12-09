package com.courierdriver.application

import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.utils.FontStyle
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.FirebaseApp

class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    lateinit var mSocket: Socket
    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        instance = this
        FacebookSdk.sdkInitialize(instance)
        FirebaseApp.initializeApp(instance)
        AppEventsLogger.activateApp(this)
        MultiDex.install(this)
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Montserrat-Regular_0.ttf")
        customFontFamily!!.addFont("semibold", "Montserrat-Medium_0.ttf")
        customFontFamily!!.addFont("bold", "Montserrat-SemiBold_0.ttf")

        try {
            mSocket = IO.socket(GlobalConstants.SOCKET_URL)
            mSocket.let {
                it.connect().on(Socket.EVENT_CONNECT) {
                    Log.d("SignallingClient", "Socket connected!!!!!")
                }
            }
        } catch (e: Exception) {
            Log.e("SOCKET", "Socket init")
        }
    }

    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance: MyApplication
    }

    fun getSocketInstance() = mSocket
}