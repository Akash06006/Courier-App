package com.example.courier.repositories.promocode

import androidx.lifecycle.MutableLiveData
import com.example.courier.R
import com.example.courier.api.ApiClient
import com.example.courier.api.ApiResponse
import com.example.courier.api.ApiService
import com.example.courier.application.MyApplication
import com.example.courier.common.UtilsFunctions
import com.example.courier.model.CommonModel
import com.example.services.model.promocode.PromoCodeListResponse
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
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<PromoCodeListResponse>(
                            "" + mResponse.body(),
                            PromoCodeListResponse::class.java
                        )
                    else {
                        gson.fromJson<PromoCodeListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            PromoCodeListResponse::class.java
                        )
                    }
                    data1!!.postValue(loginResponse)
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