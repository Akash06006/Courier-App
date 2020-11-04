package com.courierdriver.repositories

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.courierdriver.model.LoginResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class ForgotPasswordRepository {
    private var data: MutableLiveData<CommonModel>? = null
    private var forgotData: MutableLiveData<LoginResponse>? = null
    private var resendtData: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        forgotData = MutableLiveData()
        resendtData = MutableLiveData()
    }

    fun getForgotPasswordResponse(
        countryCode: String,
        phone: String
    ): MutableLiveData<LoginResponse> {
        if (phone != "") {
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "countryCode",
                countryCode
            )
            mJsonObject.addProperty("phoneNumber", phone)
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<LoginResponse>(
                                "" + mResponse.body(),
                                LoginResponse::class.java
                            )
                        else {
                            gson.fromJson<LoginResponse>(
                                mResponse.errorBody()!!.charStream(),
                                LoginResponse::class.java
                            )
                        }

                        forgotData!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        forgotData!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().callForgotPassword(mJsonObject/*countryCode, phone*/)
            )

        }
        return forgotData!!

    }

    fun getVerifyOTPResponse(jsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }


                        data!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        data!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().otpVerify(jsonObject)
            )

        }
        return data!!

    }
    /*fun getResendResponse(email : String?) : MutableLiveData<CommonModel> {
        if (email != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }


                        resendtData!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        resendtData!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().callForgotPassword(email)
            )
        }
        return resendtData!!

    }*/

}