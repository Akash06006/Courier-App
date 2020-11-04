package com.courierdriver.repositories

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class ResetPasswordRepository {
    private var data: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()

    }

    fun getResetPasswordResponse(jsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val commonResponse = if (mResponse.body() != null)
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

                        data!!.postValue(commonResponse)

                    }

                    override fun onError(mKey: String) {
                        data!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().resetPassword(jsonObject)
            )

        }
        return data!!

    }

    fun getChangePasswordResponse(jsonObject: JsonObject): MutableLiveData<CommonModel> {
        if (jsonObject.toString() != "{}") {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val commonResponse = gson.fromJson<CommonModel>(
                            "" + mResponse.body(),
                            CommonModel::class.java
                        )

                        data!!.postValue(commonResponse)

                    }

                    override fun onError(mKey: String) {
                        data!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().changePassword(jsonObject)
            )

        }
        return data!!

    }

}