package com.example.courier.repositories

import androidx.lifecycle.MutableLiveData
import com.example.courier.R
import com.example.courier.api.ApiClient
import com.example.courier.api.ApiResponse
import com.example.courier.api.ApiService
import com.example.courier.application.MyApplication
import com.example.courier.common.UtilsFunctions
import com.example.courier.model.CommonModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class ResetPasswordRepository {
    private var data : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()

    }

    fun getResetPasswordResponse(jsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        data!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().resetPassword(jsonObject)
            )

        }
        return data!!

    }

    fun getChangePasswordResponse(jsonObject : JsonObject) : MutableLiveData<CommonModel> {
        if (jsonObject.toString() != "{}") {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val commonResponse = gson.fromJson<CommonModel>(
                            "" + mResponse.body(),
                            CommonModel::class.java
                        )

                        data!!.postValue(commonResponse)

                    }

                    override fun onError(mKey : String) {
                        data!!.postValue(null)
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))

                    }

                }, ApiClient.getApiInterface().changePassword(jsonObject)
            )

        }
        return data!!

    }

}