package com.courierdriver.views.home

import android.view.View
import androidx.core.content.ContextCompat
import com.courierdriver.R
import com.courierdriver.databinding.ActivityLandingActivtyBinding
import com.courierdriver.utils.BaseActivity
import com.courierdriver.views.home.fragments.HomeFragment
import com.courierdriver.views.profile.ProfileFragment

class LandingActivty : BaseActivity(), View.OnClickListener {
    private lateinit var activityOtpVerificationBinding: ActivityLandingActivtyBinding

    override fun getLayoutId(): Int {
        return R.layout.activity_landing_activty
    }

    override fun initViews() {
        activityOtpVerificationBinding = viewDataBinding as ActivityLandingActivtyBinding
        val fragment = HomeFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")

        activityOtpVerificationBinding.imgOrderParent.setOnClickListener(this)
        activityOtpVerificationBinding.imgProfileParent.setOnClickListener(this)
        activityOtpVerificationBinding.imgMessageParent.setOnClickListener(this)
        activityOtpVerificationBinding.imgAvailableParent.setOnClickListener(this)

/*
        activityOtpVerificationBinding!!.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab?) {
                var fragment : Fragment? = null
                fragment = HomeFragment()
                //   activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE
                when (tab!!.position) {
                    0 -> {
                        */
/* activityOtpVerificationBinding!!.toolbarCommon.imgToolbarText.setText(
                             resources.getString(
                                 R.string.home
                             )
                         )*//*

                        fragment = HomeFragment()
                    }
                    1 -> {
                        fragment = ProfileFragment()
                    }
                    2 -> {
                        showToastSuccess("Coming Soon")
                    }
                    3 -> {
                        showToastSuccess("Coming Soon")
                    }
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")
                */
/* Handler().postDelayed({
                     setHeadings()
                 }, 300)*//*


            }

            override fun onTabUnselected(tab : TabLayout.Tab?) {

            }

            override fun onTabReselected(tab : TabLayout.Tab?) {
                //var fragment : Fragment? = null
                //Not In use
            }
        })
*/
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgOrderParent -> {
                visilility(View.VISIBLE, View.GONE, View.GONE, View.GONE)
                newCallFragments(HomeFragment(), supportFragmentManager)
                color(
                    R.color.colorWhite,
                    R.color.colorLiteWhite,
                    R.color.colorLiteWhite,
                    R.color.colorLiteWhite
                )
            }
            R.id.imgProfileParent -> {
                visilility(View.GONE, View.VISIBLE, View.GONE, View.GONE)
                newCallFragments(ProfileFragment(), supportFragmentManager)
                color(
                    R.color.colorLiteWhite,
                    R.color.colorWhite,
                    R.color.colorLiteWhite,
                    R.color.colorLiteWhite
                )
            }
            R.id.imgMessageParent -> {
                showToastSuccess(getString(R.string.coming_soon))
                /*visilility(View.GONE, View.GONE, View.VISIBLE, View.GONE)
                var frag = NotificationFragment()
                newCallFragments(frag, supportFragmentManager)
                color(R.color.colorLiteWhite, R.color.colorLiteWhite, R.color.colorWhite, R.color.colorLiteWhite)*/
            }
            R.id.rel_available -> {
                showToastSuccess(getString(R.string.coming_soon))
                /* visilility(View.GONE, View.GONE, View.GONE, View.VISIBLE)
                 newCallFragments(SettingFragment(), supportFragmentManager)
                 color(R.color.colorLiteWhite, R.color.colorLiteWhite, R.color.colorLiteWhite, R.color.colorWhite)*/
            }
        }
    }

    fun visilility(order: Int, profile: Int, message: Int, availble: Int) {
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
        );
        activityOtpVerificationBinding.imgProfile.setColorFilter(
            ContextCompat.getColor(this, profileColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        );
        activityOtpVerificationBinding.imgMessage.setColorFilter(
            ContextCompat.getColor(this, notificationColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        );
        activityOtpVerificationBinding.imgAvailable.setColorFilter(
            ContextCompat.getColor(this, availbleColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        );


        //activityOtpVerificationBinding.imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

}
