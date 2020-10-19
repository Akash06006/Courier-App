package com.example.courier.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.example.courier.R
import com.example.courier.application.MyApplication
import com.example.courier.common.UtilsFunctions
import com.example.courier.constants.GlobalConstants
import com.example.courier.model.CommonModel
import com.example.courier.model.LoginResponse
import com.example.courier.repositories.LoginRepository
import com.example.courier.sharedpreference.SharedPrefClass
import com.example.courier.utils.ValidationsClass
import com.example.courier.views.authentication.ForgotPasswrodActivity
import com.google.gson.JsonObject

class LoginViewModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private var loginResponse : MutableLiveData<LoginResponse>? = null
    private var signupResponse : MutableLiveData<LoginResponse>? = null
    private var verifyUserResponse : MutableLiveData<CommonModel>? = null
    private var loginRepository = LoginRepository()
    private var sharedPrefClass = SharedPrefClass()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val postEmailError = MutableLiveData<String>()
    private val postPassError = MutableLiveData<String>()

    init {
        loginResponse = loginRepository.getLoginData(null)
        signupResponse = loginRepository.callSignupApi(null)
        verifyUserResponse = loginRepository.callVerifyUserApi(null)
    }

    fun getLoginRes() : LiveData<LoginResponse> {
        return loginResponse!!
    }

    fun getVerifyUserRes() : LiveData<CommonModel> {
        return verifyUserResponse!!
    }

    fun getSignupRes() : LiveData<LoginResponse> {
        return signupResponse!!
    }

    fun getEmailError() : LiveData<String> {
        return postEmailError
    }

    fun getPasswordError() : LiveData<String> {
        return postPassError
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    /* override fun isClick() : LiveData<String> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }
    override fun clickListener(v : View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }*/
    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun callLoginApi(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            loginResponse = loginRepository.getLoginData(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun callSignupApi(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            signupResponse = loginRepository.callSignupApi(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun callVerifyUserApi(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            verifyUserResponse = loginRepository.callVerifyUserApi(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }
}