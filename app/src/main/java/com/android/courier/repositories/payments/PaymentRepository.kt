package com.android.courier.repositories.payments

import androidx.lifecycle.MutableLiveData
import com.android.courier.R
import com.android.courier.api.ApiClient
import com.android.courier.api.ApiResponse
import com.android.courier.api.ApiService
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.LoginResponse
import com.android.courier.model.payments.TransactionResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class PaymentRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var transactionList : MutableLiveData<TransactionResponse>? = null

    init {
        transactionList = MutableLiveData()

    }

    fun getTransactionList(
    ) : MutableLiveData<TransactionResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<TransactionResponse>(
                            "" + mResponse.body(),
                            TransactionResponse::class.java
                        )
                    else {
                        gson.fromJson<TransactionResponse>(
                            mResponse.errorBody()!!.charStream(),
                            TransactionResponse::class.java
                        )
                    }
                    transactionList!!.postValue(loginResponse)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    transactionList!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getTansactionHistory()
        )
        // }
        return transactionList!!

    }
    ///api/mobile/listLoyalityPoints
}