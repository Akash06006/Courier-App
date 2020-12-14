package com.android.courier.viewmodels.contactus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.viewmodels.BaseViewModel
import com.example.services.repositories.faq.ContactUsRepository
import com.google.gson.JsonObject

class ContactUsViewModel : BaseViewModel() {
    private var addConcern = MutableLiveData<CommonModel>()
    private var faqRepository = ContactUsRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnected()) {
            addConcern = faqRepository.addConcern(null)
            //clearAllNotifications = notificationRepository.clearAllNotifications("")
        }
    }

    fun addConcernRes() : LiveData<CommonModel> {
        return addConcern
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

    fun addConcern(obj : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            addConcern = faqRepository.addConcern(obj)
            mIsUpdating.postValue(true)
        }
    }

}