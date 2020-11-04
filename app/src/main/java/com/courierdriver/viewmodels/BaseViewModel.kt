package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    abstract fun isLoading(): LiveData<Boolean>
    abstract fun isClick(): LiveData<String>
    abstract fun clickListener(v: View)


}