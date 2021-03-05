package com.android.courier.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.android.courier.model.order.CreateOrdersInput
import com.android.courier.utils.FontStyle
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.github.tamir7.contacts.Contacts
//import com.google.firebase.crashlytics.FirebaseCrashlytics

class MyApplication : MultiDexApplication() {
    private var customFontFamily : FontStyle? = null
    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        instance = this
        //val crashlytics = FirebaseCrashlytics.getInstance()
       // crashlytics.log("my message")
        Contacts.initialize(this)
        FacebookSdk.sdkInitialize(instance)
        AppEventsLogger.activateApp(this)
        MultiDex.install(this)
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Montserrat-Regular_0.ttf")
        customFontFamily!!.addFont("semibold", "Montserrat-Medium_0.ttf")
        customFontFamily!!.addFont("bold", "Montserrat-SemiBold_0.ttf")
        /*customFontFamily!!.addFont("regular", "OpenSans-Regular.ttf")
        customFontFamily!!.addFont("semibold", "OpenSans-SemiBold.ttf")
        customFontFamily!!.addFont("bold", "OpenSans-Bold.ttf")*/
        //val createOrdersInput = CreateOrdersInput()
    }

    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance : MyApplication

        @get:Synchronized
        lateinit var createOrdersInput : CreateOrdersInput
    }

}