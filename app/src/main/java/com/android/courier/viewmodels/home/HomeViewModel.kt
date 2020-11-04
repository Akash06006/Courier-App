package com.android.courier.viewmodels.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.viewmodels.BaseViewModel
import com.android.services.repositories.home.HomeRepository

class HomeViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var homeRepository = HomeRepository()
    private var clearCart = MutableLiveData<CommonModel>()
    /*private var jobsHistoryResponse = MutableLiveData<JobsResponse>()
    private var acceptRejectJob = MutableLiveData<CommonModel>()
    private var startCompleteJob = MutableLiveData<CommonModel>()*/

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            /* categoriesList = homeRepository.getCategories("")
             subServicesList = homeRepository.getSubServices("")
             clearCart = homeRepository.clearCart("")*/
        }

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
    /* fun getSubServices(mJsonObject: String) {
         if (UtilsFunctions.isNetworkConnected()) {
             subServicesList = homeRepository.getSubServices(mJsonObject)
             mIsUpdating.postValue(true)
         }

     }*/

}