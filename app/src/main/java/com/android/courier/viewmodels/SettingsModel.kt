package com.android.courier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.ProfileModel
import com.android.courier.repositories.SettingsRepository
import com.android.courier.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject

class SettingsModel : BaseViewModel() {
    private var data : MutableLiveData<ProfileModel>? = null
    private var settingsRepository : SettingsRepository? = null
    private var sharedPrefClass : SharedPrefClass? = null
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        settingsRepository = SettingsRepository()
        sharedPrefClass = SharedPrefClass()
        data = settingsRepository!!.getProfileData()
        if (UtilsFunctions.isNetworkConnectedReturn()) mIsUpdating.postValue(true)
    }

    val getProfileReposne : LiveData<ProfileModel>
        get() = data!!

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.postValue(v.resources.getResourceName(v.id).split("/")[1])

    }

    fun updateAlerts(booking : Boolean, other : Boolean, auto : Boolean, redeem : Boolean) {
        val mJsonObject = JsonObject()
        mJsonObject.addProperty("booking_alert", booking)
        mJsonObject.addProperty("other_alert", other)
        mJsonObject.addProperty("auto_checkin", auto)
        mJsonObject.addProperty("auto_redeem_coupon", redeem)
        if (UtilsFunctions.isNetworkConnected()) {
            settingsRepository?.updateUserSettings(mJsonObject)
        }
    }

}