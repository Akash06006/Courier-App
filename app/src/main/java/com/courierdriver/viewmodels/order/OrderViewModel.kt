package com.courierdriver.viewmodels.order

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.order.ListsResponse
import com.courierdriver.repositories.order.OrderRepository
import com.courierdriver.viewmodels.BaseViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class OrderViewModel : BaseViewModel() {
    private var listsResponse = MutableLiveData<ListsResponse>()
    private var data = MutableLiveData<LoginResponse>()
    private var profileDetail = MutableLiveData<LoginResponse>()
    private var orderRepository = OrderRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
       // if (UtilsFunctions.isNetworkConnected()) {
            profileDetail = orderRepository.getUserProfile(null)
            data = orderRepository.updateUserProfile(null, null)
            listsResponse = orderRepository.getDataLists()
      //  }

    }

    fun getDetail(): LiveData<LoginResponse> {
        return profileDetail
    }

    fun getUpdateDetail(): LiveData<LoginResponse> {
        return data
    }

    fun getListsRes(): LiveData<ListsResponse> {
        return listsResponse
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getProfileDetail(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            profileDetail = orderRepository.getUserProfile(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun updateProfile(hashMap: HashMap<String, RequestBody>, image: MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = orderRepository.updateUserProfile(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

    fun getRegions(hashMap: HashMap<String, RequestBody>, image: MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = orderRepository.updateUserProfile(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

}