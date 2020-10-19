package com.example.courier.repositories.notifications

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.courier.R
import com.example.courier.api.ApiClient
import com.example.courier.api.ApiResponse
import com.example.courier.api.ApiService
import com.example.courier.application.MyApplication
import com.example.courier.common.UtilsFunctions
import com.example.courier.model.CommonModel
import com.example.courier.model.LoginResponse
import com.example.courier.model.notifications.NotificationsResponse
import com.example.courier.model.order.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class NotificationRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var paymentStatus : MutableLiveData<NotificationsResponse>? = null

    init {
        paymentStatus = MutableLiveData()

    }

    fun getDataLists(
    ) : MutableLiveData<NotificationsResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<NotificationsResponse>(
                            "" + mResponse.body(),
                            NotificationsResponse::class.java
                        )
                    else {
                        gson.fromJson<NotificationsResponse>(
                            mResponse.errorBody()!!.charStream(),
                            NotificationsResponse::class.java
                        )
                    }


                    paymentStatus!!.postValue(loginResponse)

                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    paymentStatus!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getNotificationLists()
        )
        // }
        return paymentStatus!!

    }

}