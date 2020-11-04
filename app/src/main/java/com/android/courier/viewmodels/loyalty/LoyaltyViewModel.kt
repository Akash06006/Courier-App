package com.android.courier.viewmodels.loyalty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.loyalty.LoyaltyResponse
import com.android.courier.model.notifications.NotificationsResponse
import com.android.courier.repositories.loyalty.LoyaltyRepository
import com.android.courier.viewmodels.BaseViewModel

class LoyaltyViewModel : BaseViewModel() {
    private var listsResponse = MutableLiveData<LoyaltyResponse>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var loyaltyRepository = LoyaltyRepository()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            listsResponse = loyaltyRepository.getDataLists()
        }
    }

    fun getList() : LiveData<LoyaltyResponse> {
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