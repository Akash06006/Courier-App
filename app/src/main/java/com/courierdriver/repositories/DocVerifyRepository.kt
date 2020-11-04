package com.courierdriver.repositories

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.profile.RegionResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.*

class DocVerifyRepository {
    private var data: MutableLiveData<LoginResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3: MutableLiveData<RegionResponse>? = null

    init {
        data = MutableLiveData()
        data3 = MutableLiveData()
    }

    fun docVerify(
        hashMap: HashMap<String, RequestBody>?,
        poaFront: MultipartBody.Part?,
        poaBack: MultipartBody.Part?,
        licenseFront: MultipartBody.Part?,
        licenseBack: MultipartBody.Part?,
        panCard: MultipartBody.Part?
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

                },
                ApiClient.getApiInterface()
                    .callverifyDoc(hashMap, poaFront, poaBack, licenseFront, licenseBack, panCard)
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