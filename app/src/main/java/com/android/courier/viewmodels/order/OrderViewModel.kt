package com.android.courier.viewmodels.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.android.courier.model.order.*
import com.android.courier.repositories.order.OrderRepository
import com.android.courier.viewmodels.BaseViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class OrderViewModel : BaseViewModel() {
    private var listsResponse = MutableLiveData<ListsResponse>()
    private var orderList = MutableLiveData<OrdersListResponse>()
    private var data = MutableLiveData<LoginResponse>()
    private var calculatePrice = MutableLiveData<CalculatePriceResponse>()
    private var applyCoupon = MutableLiveData<ApplyCouponResponse>()
    private var removeCoupon = MutableLiveData<CommonModel>()
    private var createOrder = MutableLiveData<CreateOrderResponse>()
    private var paymentStatus = MutableLiveData<CommonModel>()
    private var orderDetail = MutableLiveData<OrdersDetailResponse>()
    private var cancelReason = MutableLiveData<CancelReasonsListResponse>()
    private var cancelOrder = MutableLiveData<CommonModel>()
    private var profileDetail = MutableLiveData<LoginResponse>()
    private var orderRepository = OrderRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            profileDetail = orderRepository.getUserProfile(null)
            data = orderRepository.updateUserProfile(null, null)
            listsResponse = orderRepository.getDataLists()
            orderList = orderRepository.getOrderList("")

            calculatePrice = orderRepository.calculatePrice(null)
            applyCoupon = orderRepository.applyCoupon(null)
            removeCoupon = orderRepository.removeCoupon(null)
            orderDetail = orderRepository.orderDetail("")
            cancelReason = orderRepository.cancelReason("")
            cancelOrder = orderRepository.cancelOrder(null)
            data = orderRepository.callDistanceApi(null)
            createOrder = orderRepository.createOrder(null)
            paymentStatus = orderRepository.updatePaymentStatus(null)
        }

    }

    fun getDetail() : LiveData<LoginResponse> {
        return profileDetail
    }

    fun calculatePriceRes() : LiveData<CalculatePriceResponse> {
        return calculatePrice
    }

    fun applyCouponRes() : LiveData<ApplyCouponResponse> {
        return applyCoupon
    }

    fun removeCouponRes() : LiveData<CommonModel> {
        return removeCoupon
    }

    fun paymentStatusRes() : LiveData<CommonModel> {
        return paymentStatus
    }

    fun createOrderRes() : LiveData<CreateOrderResponse> {
        return createOrder
    }

    fun cancelOrderRes() : LiveData<CommonModel> {
        return cancelOrder
    }

    fun orderDetailRes() : LiveData<OrdersDetailResponse> {
        return orderDetail
    }

    fun cancelReasonRes() : LiveData<CancelReasonsListResponse> {
        return cancelReason
    }

    fun getUpdateDetail() : LiveData<LoginResponse> {
        return data
    }

    fun getListsRes() : LiveData<ListsResponse> {
        return listsResponse
    }

    fun orderListRes() : LiveData<OrdersListResponse> {
        return orderList
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getProfileDetail(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            profileDetail = orderRepository.getUserProfile(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun calculatePrice(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            calculatePrice = orderRepository.calculatePrice(mJsonObject)
            mIsUpdating.postValue(true)

        }
    }

    fun applyCoupon(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            applyCoupon = orderRepository.applyCoupon(mJsonObject)
            mIsUpdating.postValue(true)

        }
    }

    fun removeCoupon(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            removeCoupon = orderRepository.removeCoupon(mJsonObject)
            mIsUpdating.postValue(true)

        }
    }

    fun cancelOrder(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            cancelOrder = orderRepository.cancelOrder(mJsonObject)
            mIsUpdating.postValue(true)

        }
    }

    fun getOrderList(orderStatus : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            orderList = orderRepository.getOrderList(orderStatus)
            mIsUpdating.postValue(true)

        }
    }

    fun orderDetail(orderStatus : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            orderDetail = orderRepository.orderDetail(orderStatus)
            mIsUpdating.postValue(true)

        }
    }

    fun cancelReason(orderStatus : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            cancelReason = orderRepository.cancelReason(orderStatus)
            mIsUpdating.postValue(true)

        }
    }

    fun createOrder(createOrderInput : CreateOrdersInput) {
        if (UtilsFunctions.isNetworkConnected()) {
            createOrder = orderRepository.createOrder(createOrderInput)
            mIsUpdating.postValue(true)

        }
    }

    fun getRegions(hashMap : HashMap<String, RequestBody>, image : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = orderRepository.updateUserProfile(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

    fun updatePaymentStatus(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            paymentStatus = orderRepository.updatePaymentStatus(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun callDistanceAPi(mapQuery : java.util.HashMap<String?, String?>) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = orderRepository.callDistanceApi(mapQuery)
            mIsUpdating.postValue(true)

        }
    }

}