package com.android.courier.viewmodels.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.android.courier.model.notifications.NotificationsResponse
import com.android.courier.model.order.*
import com.android.courier.repositories.notifications.NotificationRepository
import com.android.courier.repositories.order.OrderRepository
import com.android.courier.viewmodels.BaseViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NotificaionViewModel : BaseViewModel() {
    private var listsResponse = MutableLiveData<NotificationsResponse>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var orderRepository = NotificationRepository()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            listsResponse = orderRepository.getDataLists()
        }
    }

    fun getList() : LiveData<NotificationsResponse> {
        return listsResponse
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

}