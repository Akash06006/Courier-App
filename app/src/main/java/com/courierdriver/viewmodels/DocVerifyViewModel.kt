package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.profile.RegionResponse
import com.courierdriver.repositories.DocVerifyRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DocVerifyViewModel : BaseViewModel() {
    private var regionResponse = MutableLiveData<RegionResponse>()
    private var data = MutableLiveData<LoginResponse>()
    private var docVerifyRepository = DocVerifyRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            data = docVerifyRepository.docVerify(null, null, null, null, null, null)
            //  regionResponse = profileRepository.getRegoins()
        }

    }


    fun getDocVerify(): LiveData<LoginResponse> {
        return data
    }

    fun getRegionsRes(): LiveData<RegionResponse> {
        return regionResponse
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }


    fun hitDocVerifyApi(
        hashMap: HashMap<String, RequestBody>,
        poaFront: MultipartBody.Part?,
        poaBack: MultipartBody.Part?,
        licenseFront: MultipartBody.Part?,
        licenseBack: MultipartBody.Part?,
        panCard: MultipartBody.Part?
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = docVerifyRepository.docVerify(
                hashMap,
                poaFront,
                poaBack,
                licenseFront,
                licenseBack,
                panCard
            )
            mIsUpdating.postValue(true)

        }
    }

    /*fun getRegions(hashMap : HashMap<String, RequestBody>, image : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = docVerifyRepository.getRegoins()
            mIsUpdating.postValue(true)

        }
    }*/

}