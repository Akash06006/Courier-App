package com.android.courier.views

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.android.courier.R
import com.android.courier.application.MyApplication
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivitySplashBinding
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.views.authentication.LoginActivity
import com.android.courier.views.authentication.OTPVerificationActivity
import com.android.courier.views.contacts.ContactListActivity
import com.android.courier.views.home.LandingActivty
import com.android.courier.views.orders.OrderDetailActivity
import com.android.courier.views.tutorials.TutorialActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import java.util.*

class SplashActivity : BaseActivity() {
    private var mActivitySplashBinding : ActivitySplashBinding? = null
    private var sharedPrefClass : SharedPrefClass? = null
    private var mContext : Context? = null

    override fun getLayoutId() : Int {
        return R.layout.activity_splash
    }

    override fun initViews() {
        mContext = this
        mActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)


        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                    //finish()
                }).executeAsync()
        }

        sharedPrefClass = SharedPrefClass()
        // val token : String? = "sd"
        GlobalConstants.NOTIFICATION_TOKEN = "" + SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.NOTIFICATION_TOKEN
        )
        if (TextUtils.isEmpty(GlobalConstants.NOTIFICATION_TOKEN) || GlobalConstants.NOTIFICATION_TOKEN.equals(
                "null"
            )
        ) {
            //  token = "sd"
            GlobalConstants.NOTIFICATION_TOKEN = "sd"
        }
        //if (GlobalConstants.NOTIFICATION_TOKEN != null) {
        sharedPrefClass!!.putObject(
            applicationContext,
            GlobalConstants.NOTIFICATION_TOKEN,
            GlobalConstants.NOTIFICATION_TOKEN
        )

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkScreenType()
                }
            }
        }, 3000)
    }

    private fun checkScreenType() {
        var login = ""
        if (checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance,
                    "isLogin"
                )
            )
        )
            login = sharedPrefClass!!.getPrefValue(this, "isLogin").toString()
        val tutorialVisible =
            sharedPrefClass!!.getPrefValue(this, GlobalConstants.TUTORIAL_VISIBLE).toString()
        val intent = if (login == "true") {
            Intent(this, LandingActivty::class.java)
            //Intent(this, OTPVerificationActivity::class.java)
        } else {
            if (tutorialVisible.equals("true")) {
                Intent(this, LoginActivity::class.java)
            } else {
                Intent(this, TutorialActivity::class.java)
            }

        }

        startActivity(intent)
        finish()
    }

}
