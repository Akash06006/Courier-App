package com.courierdriver.repositories.profile

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.profile.RegionResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.*

class ProfileRepository {
    private var data: MutableLiveData<LoginResponse>? = null
    private var data1: MutableLiveData<LoginResponse>? = null
    private var data2: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3: MutableLiveData<RegionResponse>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
        data3 = MutableLiveData()

    }

    fun getLoginData(jsonObject: JsonObject?): MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
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


                        data!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().calldriverLogin(jsonObject)
            )

        }
        return data!!

    }

    fun getUserProfile(jsonObject: JsonObject?): MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
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
                        data1!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getProfile(/*jsonObject*/)
            )

        }
        return data1!!

    }

    fun getLogoutResonse(jsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val logoutResponse = if (mResponse.body() != null)
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

                        data2!!.postValue(logoutResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogout(jsonObject)
            )

        }
        return data2!!

    }

    fun updateUserProfile(
        hashMap: HashMap<String, RequestBody>?,
        image: MultipartBody.Part?
    ): MutableLiveData<LoginResponse> {
        if (hashMap != null) {
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


                        data!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callUpdateProfile(hashMap, image)
            )
        }
        return data!!

    }

    fun getRegoins(
    ): MutableLiveData<RegionResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<RegionResponse>(
                            "" + mResponse.body(),
                            RegionResponse::class.java
                        )
                    else {
                        gson.fromJson<RegionResponse>(
                            mResponse.errorBody()!!.charStream(),
                            RegionResponse::class.java
                        )
                    }


                    data3!!.postValue(loginResponse)

                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data3!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getRegions()
        )
        // }
        return data3!!

    }

}