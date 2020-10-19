package com.example.services.repositories.home

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.courier.model.CommonModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class HomeRepository {
    private var data1 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data1 = MutableLiveData()
    }

}