package com.example.courier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.TextUtils
import android.view.View
import com.example.courier.R
import com.example.courier.application.MyApplication
import com.example.courier.common.UtilsFunctions
import com.example.courier.constants.GlobalConstants
import com.example.courier.model.CommonModel
import com.example.courier.repositories.ForgotPasswordRepository
import com.example.courier.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject

class OTPVerificationModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private var data : MutableLiveData<CommonModel>? = null
    private var resendResponse : MutableLiveData<CommonModel>? = null
    private var forgotPasswordRepository = ForgotPasswordRepository()
    private var sharedPrefClass = SharedPrefClass()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val otpViewError = MutableLiveData<String>()

    init {
        data = forgotPasswordRepository.getVerifyOTPResponse(null)
        //resendResponse = forgotPasswordRepository.getResendResponse(null)

    }

    fun getVerifyResponse() : LiveData<CommonModel> {
        return data!!
    }

    fun getOtpError() : LiveData<String> {
        return otpViewError
    }

    fun getForgotPasswordResponse() : LiveData<CommonModel> {
        return resendResponse!!
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    val loading : LiveData<Boolean>
        get() = mIsUpdating

    fun clickListener(
        v : View,
        code : com.goodiebag.pinview.Pinview
    ) {
        when (v.id) {
            R.id.btn_send -> {
                val otp = code.value
                if (TextUtils.isEmpty(otp)) run {
                    otpViewError.value =
                        MyApplication.instance.getString(R.string.empty) + " " + MyApplication.instance.getString(
                            R.string.OTP
                        )
                } else if (otp.length < 6) {
                    otpViewError.value = MyApplication.instance.getString(R.string.invalid) + " OTP"


                    run {
                        otpViewError.value =
                            MyApplication.instance.getString(R.string.invalid) + " " + MyApplication.instance.getString(
                                R.string.OTP
                            )
                    }
                } else {
                    val mJsonObject = JsonObject()
                    mJsonObject.addProperty("otp", otp)
                    mJsonObject.addProperty(
                        "email",
                        sharedPrefClass.getPrefValue(
                            MyApplication.instance,
                            "user_email"
                        ) as String
                    )

                    if (UtilsFunctions.isNetworkConnected()) {
                        data = forgotPasswordRepository.getVerifyOTPResponse(mJsonObject)
                        mIsUpdating.postValue(true)
                    }
                }

            }
            R.id.tv_resend -> {
                val mJsonObject = JsonObject()
                val email = sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    "user_email"
                ) as String
                mJsonObject.addProperty(
                    "email", email
                )
                if (UtilsFunctions.isNetworkConnected()) {
                  //  resendResponse = forgotPasswordRepository.getResendResponse(email)
                    mIsUpdating.postValue(true)

                }
            }
        }

    }
}