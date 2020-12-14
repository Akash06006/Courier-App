package com.android.courier.viewmodels.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.order.ListsResponse
import com.android.courier.repositories.order.OrderRepository
import com.android.courier.viewmodels.BaseViewModel
import com.android.services.repositories.home.HomeRepository
import com.google.gson.JsonObject

class HomeViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var homeRepository = HomeRepository()
    private var clearCart = MutableLiveData<CommonModel>()
    private var listsResponse = MutableLiveData<ListsResponse>()
    private var orderRepository = OrderRepository()
    private var addDriverRating = MutableLiveData<CommonModel>()
    /*private var jobsHistoryResponse = MutableLiveData<JobsResponse>()
    private var acceptRejectJob = MutableLiveData<CommonModel>()
    private var startCompleteJob = MutableLiveData<CommonModel>()*/

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            listsResponse = orderRepository.getDataLists()
            addDriverRating = homeRepository.addDriverRating(null)
        }

    }

    fun getListsRes() : LiveData<ListsResponse> {
        return listsResponse
    }

    fun getDriverRatingRes() : LiveData<CommonModel> {
        return addDriverRating
    }

    /*

   fun getGetSubServices(): LiveData<CategoriesListResponse> {
       return subServicesList
   }*/
    fun getClearCartRes() : LiveData<CommonModel> {
        return clearCart
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getList() {
        if (UtilsFunctions.isNetworkConnected()) {
            listsResponse = orderRepository.getDataLists()
            mIsUpdating.postValue(true)
        }

    }

    fun addDriverRating(s : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            clearCart = homeRepository.addDriverRating(s)
            mIsUpdating.postValue(true)
        }

    }

}