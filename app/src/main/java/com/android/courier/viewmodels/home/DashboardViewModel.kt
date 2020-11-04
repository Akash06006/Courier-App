package com.android.courier.viewmodels.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.model.CommonModel
import com.android.courier.repositories.LoginRepository
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class DashboardViewModel : BaseViewModel() {
    private var sharedPrefClass : SharedPrefClass? = null
    private var dataLogout : MutableLiveData<CommonModel>? = null
    private var loginRepository : LoginRepository? = null
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        loginRepository = LoginRepository()
        dataLogout = loginRepository!!.getLogoutResonse(null)
        sharedPrefClass = SharedPrefClass()

    }

    val getLogoutReposne : LiveData<CommonModel>
        get() = dataLogout!!

    fun callLogoutApi() {
        if (UtilsFunctions.isNetworkConnected()) {
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "id", sharedPrefClass!!.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.USERID
                ).toString()
            )
            val versionName = MyApplication.instance.packageManager
                .getPackageInfo(MyApplication.instance.packageName, 0).versionName
            val androidId = UtilsFunctions.getAndroidID()
            mJsonObject.addProperty("device-type", GlobalConstants.PLATFORM)
            mJsonObject.addProperty("device_id", androidId)
            mJsonObject.addProperty("app-version", versionName)
            dataLogout = loginRepository!!.getLogoutResonse(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.postValue(v.resources.getResourceName(v.id).split("/")[1])
    }

}