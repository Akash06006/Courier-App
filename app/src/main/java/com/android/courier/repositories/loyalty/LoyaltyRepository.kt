package com.android.courier.repositories.loyalty

import androidx.lifecycle.MutableLiveData
import com.android.courier.R
import com.android.courier.api.ApiClient
import com.android.courier.api.ApiResponse
import com.android.courier.api.ApiService
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.LoginResponse
import com.android.courier.model.loyalty.LoyaltyResponse
import com.android.courier.model.notifications.NotificationsResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class LoyaltyRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var loyaltyPoint : MutableLiveData<LoyaltyResponse>? = null

    init {
        loyaltyPoint = MutableLiveData()

    }

    fun getDataLists(
    ) : MutableLiveData<LoyaltyResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                private var loginResponse : Any? = null

                override fun onResponse(mResponse : Response<JsonObject>) {
                    loginResponse = if (mResponse.body() != null)
                        gson.fromJson<LoyaltyResponse>(
                            "" + mResponse.body(),
                            LoyaltyResponse::class.java
                        )
                    else {
                        if (mResponse.code() == 401) {
                            // UtilsFunctions.showToastError(mResponse.message())
                            loyaltyPoint!!.postValue(null)
                        } else {
                            gson.fromJson<LoyaltyResponse>(
                                mResponse.errorBody()!!.charStream(),
                                LoyaltyResponse::class.java
                            )

                        }

                    }
                    loyaltyPoint!!.postValue(loginResponse as LoyaltyResponse?)

                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    loyaltyPoint!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getLoyaltyLists()
        )
        // }
        return loyaltyPoint!!

    }
    ///api/mobile/listLoyalityPoints
}