package com.example.services.repositories.home

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CancelReasonModel
import com.courierdriver.model.CommonModel
import com.courierdriver.model.PaymentOptionsModel
import com.courierdriver.model.RegionListModel
import com.courierdriver.model.order.OrderListModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class HomeRepository {
    private var data1: MutableLiveData<CommonModel>? = MutableLiveData()
    private val gson = GsonBuilder().serializeNulls().create()

    fun getOrderList(
        orderStatus: String?,
        driverLat: String?,
        driverLong: String?,
        orderList: MutableLiveData<OrderListModel>?
    ): MutableLiveData<OrderListModel> {
        if (UtilsFunctions.isNetworkConnected() && orderStatus != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>)/* {
                        val data = gson.fromJson<OrderListModel>(
                            "" + mResponse.body()!!,
                            OrderListModel::class.java
                        )

                    }*/ {
                        val loginResponse =
                            if (mResponse.body() != null) gson.fromJson<OrderListModel>(
                                "" + mResponse.body()!!,
                                OrderListModel::class.java
                            )
                            else {
                                gson.fromJson<OrderListModel>(
                                    mResponse.errorBody()!!.charStream(),
                                    OrderListModel::class.java
                                )
                            }


                        orderList!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        orderList!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().orderList(orderStatus!!, driverLat!!, driverLong!!)
            )
        }
        return orderList!!
    }

    fun acceptOrder(
        jsonObject: JsonObject?,
        acceptOrderData: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected() && jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        acceptOrderData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        acceptOrderData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().acceptOrder(jsonObject)
            )
        }
        return acceptOrderData!!
    }

    fun cancelOrder(
        jsonObject: JsonObject?,
        cancelOrderData: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected() && jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        cancelOrderData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        cancelOrderData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().cancelRequests(jsonObject)
            )
        }
        return cancelOrderData!!
    }

    fun cancellationReason(cancellationReasonData: MutableLiveData<CancelReasonModel>?): MutableLiveData<CancelReasonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        if (mResponse.body() != null) {
                            val data = gson.fromJson<CancelReasonModel>(
                                "" + mResponse.body()!!,
                                CancelReasonModel::class.java
                            )
                            cancellationReasonData!!.postValue(data)
                        } else {
                            cancellationReasonData!!.postValue(null)
                        }
                    }

                    override fun onError(mKey: String) {
                        cancellationReasonData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().cancelReasons()
            )
        }
        return cancellationReasonData!!
    }

    fun regionList(regionListModel: MutableLiveData<RegionListModel>?): MutableLiveData<RegionListModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<RegionListModel>(
                            "" + mResponse.body()!!,
                            RegionListModel::class.java
                        )
                        regionListModel!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        regionListModel!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().getRegions()
            )
        }
        return regionListModel!!
    }

    fun profileSetup(
        jsonObject: JsonObject?,
        dataList: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected() && jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        dataList!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        dataList!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().profileSetup(jsonObject)
            )
        }
        return dataList!!
    }

    fun paymentOptions(
        dataList: MutableLiveData<PaymentOptionsModel>?
    ): MutableLiveData<PaymentOptionsModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<PaymentOptionsModel>(
                            "" + mResponse.body()!!,
                            PaymentOptionsModel::class.java
                        )
                        dataList!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        dataList!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().paymentOptions()
            )
        }
        return dataList!!
    }

    fun updateAvailability(
        jsonObject: JsonObject?,
        availableData: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected() && jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        availableData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        availableData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().availability(jsonObject)
            )
        }
        return availableData!!
    }
}