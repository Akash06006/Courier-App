package com.android.courier.viewmodels.promocode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.repositories.promocode.PromoCodeRepository
import com.android.courier.viewmodels.BaseViewModel
import com.android.services.model.promocode.PromoCodeListResponse
import com.google.gson.JsonObject

class PromoCodeViewModel : BaseViewModel() {
    private var promocodeList = MutableLiveData<PromoCodeListResponse>()
    private var promoCodeRepository = PromoCodeRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            promocodeList = promoCodeRepository.promoCodeList()
        }

    }

    fun getPromoListRes() : LiveData<PromoCodeListResponse> {
        return promocodeList
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

    fun removePromoCode(code : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            var codeObject = JsonObject()
            codeObject.addProperty(
                "promoCode", code
            )
           // removePromoCode = promoCodeRepository.removePromoCode(codeObject)
            mIsUpdating.postValue(true)
        }
    }

    fun applyPromoCode(code : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            var codeObject = JsonObject()
            codeObject.addProperty(
                "promoCode", code
            )
           // applyPromoCode = promoCodeRepository.applyPromoCode(codeObject)
            mIsUpdating.postValue(true)
        }
    }

    fun getPromoList() {
        if (UtilsFunctions.isNetworkConnected()) {
            promocodeList = promoCodeRepository.promoCodeList()
            mIsUpdating.postValue(true)
        }

    }

}