package com.courierdriver.viewmodels

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.R

class ContactUsViewModel : BaseViewModel() {

    private val postNameError = MutableLiveData<String>()
    private val postEmailError = MutableLiveData<String>()
    private val postPhoneNumberError = MutableLiveData<String>()
    private val postMessageError = MutableLiveData<String>()

    override fun isLoading(): LiveData<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isClick(): LiveData<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clickListener(v: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("HardwareIds")
    fun clickListener(
        v: View,
        name: String,
        email: String,
        phoneNumber: String,
        message: String
    ) {
        // Toast.makeText(MyApplication.instance, "$email $password", Toast.LENGTH_LONG).show()
        when (v.id) {
            R.id.btn_send -> {


            }
        }
    }

}