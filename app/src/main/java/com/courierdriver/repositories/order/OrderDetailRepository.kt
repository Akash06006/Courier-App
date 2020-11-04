package com.courierdriver.repositories.order

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CancelReasonModel
import com.courierdriver.model.CommonModel
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.order.CreateOrdersInput
import com.courierdriver.model.order.ListsResponse
import com.courierdriver.model.order.OrdersDetailResponse
import com.courierdriver.model.order.OrdersListResponse
import com.example.courier.model.order.ApplyCouponResponse
import com.example.courier.model.order.CalculatePriceResponse
import com.example.courier.model.order.CancelReasonsListResponse
import com.example.courier.model.order.CreateOrderResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.*

class OrderDetailRepository {
    private var data: MutableLiveData<LoginResponse>? = null
    private var data1: MutableLiveData<LoginResponse>? = null
    private var data2: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3: MutableLiveData<ListsResponse>? = null
    private var data5: MutableLiveData<OrdersListResponse>? = null
    private var data4: MutableLiveData<CalculatePriceResponse>? = null
    private var data6: MutableLiveData<OrdersDetailResponse>? = null
    private var data7: MutableLiveData<CancelReasonsListResponse>? = null
    private var cancelOrder: MutableLiveData<CommonModel>? = null
    private var applyCoupon: MutableLiveData<ApplyCouponResponse>? = null
    private var removeCoupon: MutableLiveData<CommonModel>? = null
    private var createOrder: MutableLiveData<CreateOrderResponse>? = null
    private var paymentStatus: MutableLiveData<CommonModel>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
        data3 = MutableLiveData()
        data4 = MutableLiveData()
        data5 = MutableLiveData()
        data6 = MutableLiveData()
        data7 = MutableLiveData()
        cancelOrder = MutableLiveData()
        removeCoupon = MutableLiveData()
        createOrder = MutableLiveData()
        applyCoupon = MutableLiveData()
        paymentStatus = MutableLiveData()

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

    fun pickupOrder(
        jsonObject: JsonObject?,
        pickupOrderData: MutableLiveData<CommonModel>?
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
                        pickupOrderData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        pickupOrderData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().pickupOrder(jsonObject)
            )
        }
        return pickupOrderData!!
    }

    fun completeOrder(
        jsonObject: JsonObject?,
        completeOrderData: MutableLiveData<CommonModel>?
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
                        completeOrderData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        completeOrderData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().completeOrder(jsonObject)
            )
        }
        return completeOrderData!!
    }

    fun getLoginData(jsonObject: JsonObject?): MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
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

                }, ApiClient.getApiInterface().callLogin(jsonObject)
            )

        }
        return data!!

    }

    fun getUserProfile(jsonObject: JsonObject?): MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
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
                        data1!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getProfile(/*jsonObject*/)
            )

        }
        return data1!!

    }

    fun getLogoutResonse(jsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val logoutResponse = if (mResponse.body() != null)
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

                        data2!!.postValue(logoutResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogout(jsonObject)
            )

        }
        return data2!!

    }

    fun updateUserProfile(
        hashMap: HashMap<String, RequestBody>?,
        image: MultipartBody.Part?
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

                }, ApiClient.getApiInterface().callUpdateProfile(hashMap, image)
            )
        }
        return data!!

    }

    fun callDistanceApi(
        hashMap: HashMap<String?, String?>?
    ): MutableLiveData<LoginResponse> {
        if (hashMap != null) {
            val mApiService = ApiService<JsonObject>()
            ApiClient.getApiInterface().getDistanceInfo(hashMap)?.let {
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

                    }, it
                )
            }
        }
        return data!!

    }

    fun getDataLists(
    ): MutableLiveData<ListsResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<ListsResponse>(
                            "" + mResponse.body(),
                            ListsResponse::class.java
                        )
                    else {
                        gson.fromJson<ListsResponse>(
                            mResponse.errorBody()!!.charStream(),
                            ListsResponse::class.java
                        )
                    }


                    data3!!.postValue(loginResponse)

                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data3!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getLists()
        )
        // }
        return data3!!

    }

    fun getOrderList(
        listType: String?
    ): MutableLiveData<OrdersListResponse> {
        if (!TextUtils.isEmpty(listType)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<OrdersListResponse>(
                                "" + mResponse.body(),
                                OrdersListResponse::class.java
                            )
                        else {
                            gson.fromJson<OrdersListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                OrdersListResponse::class.java
                            )
                        }


                        data5!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data5!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().getOrderList(listType)
            )
        }
        return data5!!

    }

    fun calculatePrice(mJsonObject: JsonObject?): MutableLiveData<CalculatePriceResponse> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CalculatePriceResponse>(
                                "" + mResponse.body(),
                                CalculatePriceResponse::class.java
                            )
                        else {
                            gson.fromJson<CalculatePriceResponse>(
                                mResponse.errorBody()!!.charStream(),
                                CalculatePriceResponse::class.java
                            )
                        }


                        data4!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data4!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().calculatePrice(mJsonObject)
            )
        }
        return data4!!

    }

    fun applyCoupon(mJsonObject: JsonObject?): MutableLiveData<ApplyCouponResponse> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<ApplyCouponResponse>(
                                "" + mResponse.body(),
                                ApplyCouponResponse::class.java
                            )
                        else {
                            gson.fromJson<ApplyCouponResponse>(
                                mResponse.errorBody()!!.charStream(),
                                ApplyCouponResponse::class.java
                            )
                        }


                        applyCoupon!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        applyCoupon!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().applyCoupon(mJsonObject)
            )
        }
        return applyCoupon!!

    }

    fun removeCoupon(mJsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
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


                        removeCoupon!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        removeCoupon!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().removeCoupon(mJsonObject)
            )
        }
        return removeCoupon!!

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


    fun orderDetail(orderId: String?): MutableLiveData<OrdersDetailResponse> {
        if (!TextUtils.isEmpty(orderId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<OrdersDetailResponse>(
                                "" + mResponse.body(),
                                OrdersDetailResponse::class.java
                            )
                        else {
                            gson.fromJson<OrdersDetailResponse>(
                                mResponse.errorBody()!!.charStream(),
                                OrdersDetailResponse::class.java
                            )
                        }


                        data6!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data6!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().orderDetail(orderId)
            )
        }
        return data6!!

    }

    fun cancelReason(orderId: String?): MutableLiveData<CancelReasonsListResponse> {
        if (!TextUtils.isEmpty(orderId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CancelReasonsListResponse>(
                                "" + mResponse.body(),
                                CancelReasonsListResponse::class.java
                            )
                        else {
                            gson.fromJson<CancelReasonsListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                CancelReasonsListResponse::class.java
                            )
                        }


                        data7!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data7!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().cancelReason()
            )
        }
        return data7!!

    }

    fun createOrder(createOrderInput: CreateOrdersInput?): MutableLiveData<CreateOrderResponse> {
        if (createOrderInput != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CreateOrderResponse>(
                                "" + mResponse.body(),
                                CreateOrderResponse::class.java
                            )
                        else {
                            gson.fromJson<CreateOrderResponse>(
                                mResponse.errorBody()!!.charStream(),
                                CreateOrderResponse::class.java
                            )
                        }

                        createOrder!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        createOrder!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().createOrder(createOrderInput)
            )
        }
        return createOrder!!

    }

    fun cancellationReason(cancellationReasonData: MutableLiveData<CancelReasonModel>?): MutableLiveData<CancelReasonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CancelReasonModel>(
                            "" + mResponse.body()!!,
                            CancelReasonModel::class.java
                        )
                        cancellationReasonData!!.postValue(data)
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

    fun updatePaymentStatus(mJsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
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
                        paymentStatus!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        paymentStatus!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().updatePaymentSuccess(mJsonObject)
            )

        }
        return paymentStatus!!

    }

}