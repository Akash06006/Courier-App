package com.example.courier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import android.view.View

abstract class BaseViewModel : ViewModel() {
     abstract fun isLoading(): LiveData<Boolean>
     abstract fun isClick(): LiveData<String>
     abstract fun clickListener(v:View)


}