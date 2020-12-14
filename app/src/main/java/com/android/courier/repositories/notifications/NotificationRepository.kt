package com.android.courier.repositories.notifications

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.courier.R
import com.android.courier.api.ApiClient
import com.android.courier.api.ApiResponse
import com.android.courier.api.ApiService
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.android.courier.model.notifications.NotificationsResponse
import com.android.courier.model.order.*
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
                private var loginResponse : Any? = null

                override fun onResponse(mResponse : Response<JsonObject>) {
                    loginResponse = if (mResponse.body() != null)
                        gson.fromJson<NotificationsResponse>(
                            "" + mResponse.body(),
                            NotificationsResponse::class.java
                        )
                    else {
                        if (mResponse.code() == 401) {
                           // UtilsFunctions.showToastError(mResponse.message())
                            paymentStatus!!.postValue(null)
                        } else {
                            gson.fromJson<NotificationsResponse>(
                                mResponse.errorBody()!!.charStream(),
                                NotificationsResponse::class.java
                            )

                        }

                    }
                    paymentStatus!!.postValue(loginResponse as NotificationsResponse?)
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