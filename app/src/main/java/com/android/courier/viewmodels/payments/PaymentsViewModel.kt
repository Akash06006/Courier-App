package com.android.courier.viewmodels.payments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.loyalty.LoyaltyResponse
import com.android.courier.model.payments.TransactionResponse
import com.android.courier.repositories.payments.PaymentRepository
import com.android.courier.viewmodels.BaseViewModel

class PaymentsViewModel : BaseViewModel() {
    private var listsResponse = MutableLiveData<TransactionResponse>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var paymentRepository = PaymentRepository()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            listsResponse = paymentRepository.getTransactionList()
        }
    }

    fun getTransactionList() : LiveData<TransactionResponse> {
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