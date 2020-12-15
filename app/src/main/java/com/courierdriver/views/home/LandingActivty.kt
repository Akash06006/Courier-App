package com.courierdriver.views.home

import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityLandingActivtyBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.broadcastReceiver.NotifyWorkStatusButtons
import com.courierdriver.utils.broadcastReceiver.WorkStatusBroadcastReceiver
import com.courierdriver.utils.broadcastReceiver.WorkStatusChangeAvailableButton
import com.courierdriver.viewmodels.home.HomeViewModel
import com.courierdriver.views.home.fragments.HomeFragment
import com.courierdriver.views.notification.NotificationChatActivity
import com.courierdriver.views.profile.ProfileHomeActivity

class LandingActivty : BaseActivity(), View.OnClickListener, NotifyWorkStatusButtons {
    private lateinit var activityOtpVerificationBinding: ActivityLandingActivtyBinding
    private lateinit var homeViewModel: HomeViewModel
    private var isAvailable = false
    private var available = "false"
    private var callFromHome = false

    override fun getLayoutId(): Int {
        return R.layout.activity_landing_activty
    }

    override fun initViews() {
        activityOtpVerificationBinding = viewDataBinding as ActivityLandingActivtyBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        val fragment = HomeFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")

        activityOtpVerificationBinding.imgOrderParent.setOnClickListener(this)
        activityOtpVerificationBinding.imgProfileParent.setOnClickListener(this)
        activityOtpVerificationBinding.imgMessageParent.setOnClickListener(this)
        activityOtpVerificationBinding.relAvailable.setOnClickListener(this)

        loaderObserver()
        setButtonsWithSharePref()
        updateAvailabilityObserver()
        subscribeWorkStatusReceiver()
        subscribeWorkStatusButtonReceiver()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgOrderParent -> {
                visibility(View.VISIBLE, View.GONE, View.GONE, View.GONE)
                newCallFragments(HomeFragment(), supportFragmentManager)
                color(
                    R.color.colorWhite,
                    R.color.colorLiteWhite,
                    R.color.colorLiteWhite,
                    R.color.colorLiteWhite
                )
            }
            R.id.imgProfileParent -> {
                startActivity(Intent(this, ProfileHomeActivity::class.java))
                /* visilility(View.GONE, View.VISIBLE, View.GONE, View.GONE)
                 newCallFragments(ProfileHomeFragment(), supportFragmentManager)
                 color(
                     R.color.colorLiteWhite,
                     R.color.colorWhite,
                     R.color.colorLiteWhite,
                     R.color.colorLiteWhite
                 )*/
            }
            R.id.imgMessageParent -> {
                startActivity(Intent(this, NotificationChatActivity::class.java))
                /*visilility(View.GONE, View.GONE, View.VISIBLE, View.GONE)
                var frag = NotificationFragment()
                newCallFragments(frag, supportFragmentManager)
                color(R.color.colorLiteWhite, R.color.colorLiteWhite, R.color.colorWhite, R.color.colorLiteWhite)*/
            }
            R.id.rel_available -> {
                homeViewModel.updateAvailability(isAvailable)
            }
        }
    }

    private fun subscribeWorkStatusReceiver() {
        val contractDetailsReceiver = WorkStatusBroadcastReceiver()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(contractDetailsReceiver, IntentFilter("workStatusReceiver"))
    }

    private fun subscribeWorkStatusButtonReceiver() {
        val availaibilityStatusReceiver = WorkStatusChangeAvailableButton(this)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(availaibilityStatusReceiver, IntentFilter("workStatusButtonReceiver"))
    }

    private fun setAvailabilityColor() {

        SharedPrefClass().putObject(
            MyApplication.instance,
            GlobalConstants.AVAILABLE,
            isAvailable.toString()
        )
        val workStatusData = Intent("workStatusReceiver")
        LocalBroadcastManager.getInstance(this).sendBroadcast(workStatusData)

        if (isAvailable) {
            available = "false"
            isAvailable = false
            activityOtpVerificationBinding.imgAvailable.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_not_available)
            )
            activityOtpVerificationBinding.txtAvailble.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorRed
                )
            )
            activityOtpVerificationBinding.txtAvailble.text = "Not Working"
        } else {
            isAvailable = true
            available = "true"
            activityOtpVerificationBinding.imgAvailable.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_available)
            )
            activityOtpVerificationBinding.txtAvailble.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSuccess
                )
            )
            activityOtpVerificationBinding.txtAvailble.text = "Working"
        }
    }

    private fun updateAvailabilityObserver() {
        homeViewModel.updateAvailabilityData().observe(
            this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            setAvailabilityColor()
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }


    private fun visibility(order: Int, profile: Int, message: Int, availble: Int) {
        activityOtpVerificationBinding.orderTab.visibility = order
        activityOtpVerificationBinding.profileTab.visibility = profile
        activityOtpVerificationBinding.messageTab.visibility = message
        activityOtpVerificationBinding.availbleTab.visibility = availble
    }

    fun color(
        orderColor: Int,
        profileColor: Int,
        notificationColor: Int,
        availbleColor: Int
    ) {
        activityOtpVerificationBinding.txtOrder.setTextColor(
            ContextCompat.getColor(
                this,
                orderColor
            )
        )
        activityOtpVerificationBinding.txtProfile.setTextColor(
            ContextCompat.getColor(
                this,
                profileColor
            )
        )
        activityOtpVerificationBinding.txtNotification.setTextColor(
            ContextCompat.getColor(
                this,
                notificationColor
            )
        )
        activityOtpVerificationBinding.txtAvailble.setTextColor(
            ContextCompat.getColor(
                this,
                availbleColor
            )
        )

//        activityOtpVerificationBinding.imgOrder.setColorFilter(resources.getColor(orderColor),android.graphics.PorterDuff.Mode.MULTIPLY);
//        activityOtpVerificationBinding.imgProfile.setColorFilter(resources.getColor(profileColor),android.graphics.PorterDuff.Mode.MULTIPLY);
//        activityOtpVerificationBinding.imgMessage.setColorFilter(resources.getColor(notificationColor),android.graphics.PorterDuff.Mode.MULTIPLY);
//        activityOtpVerificationBinding.imgAvailable.setColorFilter(resources.getColor(availbleColor),android.graphics.PorterDuff.Mode.MULTIPLY);

        activityOtpVerificationBinding.imgOrder.setColorFilter(
            ContextCompat.getColor(this, orderColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        activityOtpVerificationBinding.imgProfile.setColorFilter(
            ContextCompat.getColor(this, profileColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        activityOtpVerificationBinding.imgMessage.setColorFilter(
            ContextCompat.getColor(this, notificationColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        activityOtpVerificationBinding.imgAvailable.setColorFilter(
            ContextCompat.getColor(this, availbleColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )


        //activityOtpVerificationBinding.imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private fun loaderObserver() {
        homeViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    override fun refreshWorkStatusData() {
        setButtonsWithSharePref()
        //  homeViewModel.updateAvailability(isAvailable)
    }

    private fun setButtonsWithSharePref() {
        available =
            SharedPrefClass().getPrefValue(this, GlobalConstants.AVAILABLE)
                .toString()
        isAvailable = available.toBoolean()

        if (isAvailable) {
            available = "false"
            isAvailable = false
            activityOtpVerificationBinding.imgAvailable.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_available)
            )
            activityOtpVerificationBinding.txtAvailble.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSuccess
                )
            )
            activityOtpVerificationBinding.txtAvailble.text = "Working"
        } else {
            available = "true"
            isAvailable = true
            activityOtpVerificationBinding.imgAvailable.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_not_available)
            )
            activityOtpVerificationBinding.txtAvailble.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorRed
                )
            )
            activityOtpVerificationBinding.txtAvailble.text = "Not Working"
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
