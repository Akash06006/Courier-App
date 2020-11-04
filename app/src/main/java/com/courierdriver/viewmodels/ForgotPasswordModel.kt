package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.LoginResponse
import com.courierdriver.repositories.ForgotPasswordRepository
import com.courierdriver.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject

class ForgotPasswordModel : BaseViewModel() {
    private var data: MutableLiveData<LoginResponse>? = null
    private var forgotPasswordRepository: ForgotPasswordRepository? = null
    private var sharedPrefClass: SharedPrefClass? = null
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val postClick = MutableLiveData<String>()
    private val cancel = MutableLiveData<Boolean>()

    init {
        forgotPasswordRepository = ForgotPasswordRepository()
        sharedPrefClass = SharedPrefClass()
        data = forgotPasswordRepository!!.getForgotPasswordResponse(
            "", ""
        )

    }

    fun getForgotPasswordResponse(): LiveData<LoginResponse> {
        return data!!
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return postClick
    }

    override fun clickListener(v: View) {
        postClick.value = v.resources.getResourceName(v.id).split("/")[1]

    }

    fun forgotApi(countryCode: String, phone: String) {
        val mJsonObject = JsonObject()
        mJsonObject.addProperty("email", phone)
        if (UtilsFunctions.isNetworkConnected()) {
            data = forgotPasswordRepository!!.getForgotPasswordResponse(countryCode, phone)
            mIsUpdating.postValue(true)
        }
    }

    val isCancel: LiveData<Boolean>
        get() = cancel

}