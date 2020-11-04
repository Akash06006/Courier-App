package com.courierdriver.views.fragments

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.databinding.FragmentSettingsBinding
import com.courierdriver.model.ProfileModel
import com.courierdriver.utils.BaseFragment
import com.courierdriver.viewmodels.SettingsModel
import com.courierdriver.views.authentication.ChangePasswrodActivity

class SettingsFragment : BaseFragment() {
    private var fragmentSettingsBinding: FragmentSettingsBinding? = null
    private var settingsModel: SettingsModel? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_settings
    }

    override fun initView() {
        fragmentSettingsBinding = viewDataBinding as FragmentSettingsBinding
        settingsModel = ViewModelProviders.of(this).get(SettingsModel::class.java)
        fragmentSettingsBinding!!.settingsViewModel = settingsModel


        settingsModel!!.isClick().observe(
            viewLifecycleOwner, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_password_change" -> {
                        val intent1 = Intent(this.baseActivity, ChangePasswrodActivity::class.java)
                        baseActivity.startActivity(intent1)
                    }
                    "auto_checkin" -> {
                        settingsModel!!.updateAlerts(
                            fragmentSettingsBinding!!.bookingAlert.isChecked,
                            fragmentSettingsBinding!!.otherAlert.isChecked,
                            fragmentSettingsBinding!!.autoCheckin.isChecked,
                            fragmentSettingsBinding!!.autoRedeemAlert.isChecked
                        )
                    }
                    "booking_alert" -> {
                        settingsModel!!.updateAlerts(
                            fragmentSettingsBinding!!.bookingAlert.isChecked,
                            fragmentSettingsBinding!!.otherAlert.isChecked,
                            fragmentSettingsBinding!!.autoCheckin.isChecked,
                            fragmentSettingsBinding!!.autoRedeemAlert.isChecked
                        )
                    }
                    "other_alert" -> {
                        settingsModel!!.updateAlerts(
                            fragmentSettingsBinding!!.bookingAlert.isChecked,
                            fragmentSettingsBinding!!.otherAlert.isChecked,
                            fragmentSettingsBinding!!.autoCheckin.isChecked,
                            fragmentSettingsBinding!!.autoRedeemAlert.isChecked
                        )
                    }
                    "auto_redeem_alert" -> {
                        settingsModel!!.updateAlerts(
                            fragmentSettingsBinding!!.bookingAlert.isChecked,
                            fragmentSettingsBinding!!.otherAlert.isChecked,
                            fragmentSettingsBinding!!.autoCheckin.isChecked,
                            fragmentSettingsBinding!!.autoRedeemAlert.isChecked
                        )
                    }
                }

            })
        )


        settingsModel!!.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                this.baseActivity.startProgressDialog()
            } else {
                this.baseActivity.stopProgressDialog()
            }
        })

        settingsModel!!.getProfileReposne.observe(viewLifecycleOwner,
            Observer<ProfileModel> { profileData ->
                this.baseActivity.stopProgressDialog()
                fragmentSettingsBinding!!.profileModel = profileData
            })

    }

}