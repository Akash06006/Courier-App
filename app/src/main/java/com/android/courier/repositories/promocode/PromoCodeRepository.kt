package com.android.courier.repositories.promocode

import androidx.lifecycle.MutableLiveData
import com.android.courier.R
import com.android.courier.api.ApiClient
import com.android.courier.api.ApiResponse
import com.android.courier.api.ApiService
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.loyalty.LoyaltyResponse
import com.android.services.model.promocode.PromoCodeListResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class PromoCodeRepository {
    private var data1 : MutableLiveData<PromoCodeListResponse>? = null
    private var data2 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data1 = MutableLiveData()
        data2 = MutableLiveData()
    }

    fun promoCodeList(/*mJsonObject : String*/) : MutableLiveData<PromoCodeListResponse> {
        //if (!TextUtils.isEmpty(mJsonObject)) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                private var loginResponse : Any? = null

                override fun onResponse(mResponse : Response<JsonObject>) {
                    loginResponse = if (mResponse.body() != null)
                        gson.fromJson<PromoCodeListResponse>(
                            "" + mResponse.body(),
                            PromoCodeListResponse::class.java
                        )
                    else {
                        if (mResponse.code() == 401) {
                            // UtilsFunctions.showToastError(mResponse.message())
                            data1!!.postValue(null)
                        } else {
                            gson.fromJson<PromoCodeListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                PromoCodeListResponse::class.java
                            )
                        }
                    }
                    data1!!.postValue(loginResponse as PromoCodeListResponse?)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data1!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getPromoList(/*mJsonObject*/)
        )
        //}
        return data1!!

    }

}