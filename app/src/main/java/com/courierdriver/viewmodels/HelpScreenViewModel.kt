package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.courierdriver.repositories.HelpRepository

class HelpScreenViewModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private var helpRepository: HelpRepository = HelpRepository()
    private var logoutList: MutableLiveData<CommonModel>? = MutableLiveData()

    fun logout() {
        if (UtilsFunctions.isNetworkConnected()) {
            logoutList = helpRepository.logout(logoutList)
            mIsUpdating.postValue(true)
        }
    }

    fun logoutData(): LiveData<CommonModel> {
        return logoutList!!
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return isClick
    }

    override fun clickListener(v: View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    val loading: LiveData<Boolean>
        get() = mIsUpdating
}