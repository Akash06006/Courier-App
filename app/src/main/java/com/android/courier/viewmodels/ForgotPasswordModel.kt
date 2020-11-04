package com.android.courier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.android.courier.repositories.ForgotPasswordRepository
import com.android.courier.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject

class ForgotPasswordModel : BaseViewModel() {
    private var data : MutableLiveData<LoginResponse>? = null
    private var forgotPasswordRepository : ForgotPasswordRepository? = null
    private var sharedPrefClass : SharedPrefClass? = null
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

    fun getForgotPasswordResponse() : LiveData<LoginResponse> {
        return data!!
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return postClick
    }

    override fun clickListener(v : View) {
        postClick.value = v.resources.getResourceName(v.id).split("/")[1]

    }

    fun forgotApi(countryCode : String, phone : String) {
        val mJsonObject = JsonObject()
        mJsonObject.addProperty("email", phone)
        if (UtilsFunctions.isNetworkConnected()) {
            data = forgotPasswordRepository!!.getForgotPasswordResponse(countryCode, phone)
            mIsUpdating.postValue(true)
        }
    }

    val isCancel : LiveData<Boolean>
        get() = cancel

}