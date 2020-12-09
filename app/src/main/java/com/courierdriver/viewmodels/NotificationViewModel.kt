package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.NotificationListModel
import com.courierdriver.repositories.HelpRepository

class NotificationViewModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private var helpRepository: HelpRepository = HelpRepository()
    private var notificationList: MutableLiveData<NotificationListModel>? = MutableLiveData()

    fun notification() {
        if (UtilsFunctions.isNetworkConnected()) {
            notificationList = helpRepository.getNotifications(notificationList)
            mIsUpdating.postValue(true)
        }
    }

    fun notificationData(): LiveData<NotificationListModel> {
        return notificationList!!
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