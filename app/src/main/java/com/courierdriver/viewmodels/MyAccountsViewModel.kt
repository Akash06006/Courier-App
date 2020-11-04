package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyAccountsViewModel : BaseViewModel() {
    private val btnClick = MutableLiveData<String>()
    override fun isLoading(): LiveData<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.postValue(v.resources.getResourceName(v.id).split("/")[1])
    }

}