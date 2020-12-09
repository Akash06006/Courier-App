package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.model.CommonModel
import com.courierdriver.repositories.profile.ProfileDetailsRepository

class ProfileDetailsViewModel : BaseViewModel() {
    private var data: MutableLiveData<CommonModel>? = null
    private var profileDetailsRepository = ProfileDetailsRepository()
    private val isClick = MutableLiveData<String>()
    private val mIsUpdating = MutableLiveData<Boolean>()

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