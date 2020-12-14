package com.android.courier.repositories.order

import android.content.Intent
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
import com.android.courier.model.order.*
import com.android.courier.views.authentication.LoginActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class OrderRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private var data1 : MutableLiveData<LoginResponse>? = null
    private var data2 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3 : MutableLiveData<ListsResponse>? = null
    private var data5 : MutableLiveData<OrdersListResponse>? = null
    private var data4 : MutableLiveData<CalculatePriceResponse>? = null
    private var data6 : MutableLiveData<OrdersDetailResponse>? = null
    private var data7 : MutableLiveData<CancelReasonsListResponse>? = null
    private var cancelOrder : MutableLiveData<CommonModel>? = null
    private var applyCoupon : MutableLiveData<ApplyCouponResponse>? = null
    private var removeCoupon : MutableLiveData<CommonModel>? = null
    private var createOrder : MutableLiveData<CreateOrderResponse>? = null
    private var paymentStatus : MutableLiveData<CommonModel>? = null

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

    fun getLoginData(jsonObject : JsonObject?) : MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogin(jsonObject)
            )

        }
        return data!!

    }

    fun getUserProfile(jsonObject : JsonObject?) : MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getProfile(/*jsonObject*/)
            )

        }
        return data1!!

    }

    fun getLogoutResonse(jsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogout(/*jsonObject*/)
            )

        }
        return data2!!

    }

    fun updateUserProfile(
        hashMap : HashMap<String, RequestBody>?,
        image : MultipartBody.Part?
    ) : MutableLiveData<LoginResponse> {
        if (hashMap != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callUpdateProfile(hashMap, image)
            )
        }
        return data!!

    }

    fun callDistanceApi(
        hashMap : HashMap<String?, String?>?
    ) : MutableLiveData<LoginResponse> {
        if (hashMap != null) {
            val mApiService = ApiService<JsonObject>()
            ApiClient.getApiInterface().getDistanceInfo(hashMap)?.let {
                mApiService.get(
                    object : ApiResponse<JsonObject> {
                        override fun onResponse(mResponse : Response<JsonObject>) {
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

                        override fun onError(mKey : String) {
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
    ) : MutableLiveData<ListsResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
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

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data3!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getLists()
        )
        // }
        return data3!!

    }

    fun getOrderList(
        listType : String?
    ) : MutableLiveData<OrdersListResponse> {
        if (!TextUtils.isEmpty(listType)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    private var loginResponse : Any? = null

                    override fun onResponse(mResponse : Response<JsonObject>) {
                        loginResponse = if (mResponse.body() != null)
                            gson.fromJson<OrdersListResponse>(
                                "" + mResponse.body(),
                                OrdersListResponse::class.java
                            )
                        else {
                            if (mResponse.code() == 401) {
                                UtilsFunctions.showToastError(mResponse.message())
                                val i = Intent(
                                    MyApplication.instance.applicationContext,
                                    LoginActivity::class.java
                                )
                                i.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                MyApplication.instance.applicationContext.startActivity(i)
                                return
                                data5!!.postValue(null)
                                /**/
                            } else {
                                gson.fromJson<OrdersListResponse>(
                                    mResponse.errorBody()!!.charStream(),
                                    OrdersListResponse::class.java
                                )

                            }

                        }
                        data5!!.postValue(loginResponse as OrdersListResponse?)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data5!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().getOrderList(listType)
            )
        }
        return data5!!

    }

    fun calculatePrice(mJsonObject : JsonObject?) : MutableLiveData<CalculatePriceResponse> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    private var loginResponse : Any? = null

                    override fun onResponse(mResponse : Response<JsonObject>) {
                        loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CalculatePriceResponse>(
                                "" + mResponse.body(),
                                CalculatePriceResponse::class.java
                            )
                        else {
                            if (mResponse.code() == 401) {
                                UtilsFunctions.showToastError(mResponse.message())
                                data4!!.postValue(null)
                            } else {
                                UtilsFunctions.showToastError(mResponse.message())
                                gson.fromJson<CalculatePriceResponse>(
                                    mResponse.errorBody()!!.charStream(),
                                    CalculatePriceResponse::class.java
                                )
                                data4!!.postValue(loginResponse as CalculatePriceResponse?)
                            }

                        }
                        data4!!.postValue(loginResponse as CalculatePriceResponse?)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data4!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().calculatePrice(mJsonObject)
            )
        }
        return data4!!

    }

    fun applyCoupon(mJsonObject : JsonObject?) : MutableLiveData<ApplyCouponResponse> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        applyCoupon!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().applyCoupon(mJsonObject)
            )
        }
        return applyCoupon!!

    }

    fun removeCoupon(mJsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        removeCoupon!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().removeCoupon(mJsonObject)
            )
        }
        return removeCoupon!!

    }

    fun cancelOrder(mJsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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


                        cancelOrder!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        cancelOrder!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().cancelOrder(mJsonObject)
            )
        }
        return cancelOrder!!

    }

    fun orderDetail(orderId : String?) : MutableLiveData<OrdersDetailResponse> {
        if (!TextUtils.isEmpty(orderId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    private var loginResponse : Any? = null

                    override fun onResponse(mResponse : Response<JsonObject>) {
                        loginResponse = if (mResponse.body() != null)
                            gson.fromJson<OrdersDetailResponse>(
                                "" + mResponse.body(),
                                OrdersDetailResponse::class.java
                            )
                        else {
                            if (mResponse.code() == 401) {
                                UtilsFunctions.showToastError(mResponse.message())
                                data6!!.postValue(null)
                            } else {
                                UtilsFunctions.showToastError(mResponse.message())
                                gson.fromJson<OrdersDetailResponse>(
                                    mResponse.errorBody()!!.charStream(),
                                    OrdersDetailResponse::class.java
                                )
                                //data6!!.postValue(loginResponse as OrdersDetailResponse?)
                            }

                        }
                        data6!!.postValue(loginResponse as OrdersDetailResponse?)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data6!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().orderDetail(orderId)
            )
        }
        return data6!!

    }

    fun cancelReason(orderId : String?) : MutableLiveData<CancelReasonsListResponse> {
        if (!TextUtils.isEmpty(orderId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    private var loginResponse : Any? = null

                    override fun onResponse(mResponse : Response<JsonObject>) {
                        loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CancelReasonsListResponse>(
                                "" + mResponse.body(),
                                CancelReasonsListResponse::class.java
                            )
                        else {
                            if (mResponse.code() == 401) {
                                UtilsFunctions.showToastError(mResponse.message())
                                data7!!.postValue(null)
                                return
                            } else {
                                gson.fromJson<CancelReasonsListResponse>(
                                    mResponse.errorBody()!!.charStream(),
                                    CancelReasonsListResponse::class.java
                                )

                            }

                        }
                        data7!!.postValue(loginResponse as CancelReasonsListResponse?)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data7!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().cancelReason()
            )
        }
        return data7!!

    }

    fun createOrder(createOrderInput : CreateOrdersInput?) : MutableLiveData<CreateOrderResponse> {
        if (createOrderInput != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CreateOrderResponse>(
                                "" + mResponse.body(),
                                CreateOrderResponse::class.java
                            )
                        else {
                            if (mResponse.code() == 401) {
                                UtilsFunctions.showToastError(mResponse.message())
                                val i = Intent(
                                    MyApplication.instance.applicationContext,
                                    LoginActivity::class.java
                                )
                                i.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                MyApplication.instance.applicationContext.startActivity(i)
                                /**/
                            } else {
                                gson.fromJson<CreateOrderResponse>(
                                    mResponse.errorBody()!!.charStream(),
                                    CreateOrderResponse::class.java
                                )
                            }

                        }

                        createOrder!!.postValue(loginResponse as CreateOrderResponse?)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        createOrder!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().createOrder(createOrderInput)
            )
        }
        return createOrder!!

    }

    fun updatePaymentStatus(mJsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        paymentStatus!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().updatePaymentSuccess(mJsonObject)
            )

        }
        return paymentStatus!!

    }

}