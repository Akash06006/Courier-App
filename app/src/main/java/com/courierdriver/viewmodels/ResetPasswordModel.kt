package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.courierdriver.repositories.ResetPasswordRepository
import com.courierdriver.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject

class ResetPasswordModel : BaseViewModel() {
    private var data: MutableLiveData<CommonModel>? = null
    private var resetPasswordRepository = ResetPasswordRepository()
    private var sharedPrefClass = SharedPrefClass()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val postClick = MutableLiveData<String>()
    private val mPassError = MutableLiveData<String>()
    private val mConfirmError = MutableLiveData<String>()

    init {
        data = resetPasswordRepository.getResetPasswordResponse(null)

    }

    fun getPassError(): LiveData<String> {
        return mPassError
    }

    fun getConfirmError(): LiveData<String> {
        return mConfirmError
    }

    fun getResetPasswordResponse(): LiveData<CommonModel> {
        return data!!
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return postClick
    }

    override fun clickListener(v: View) {
        postClick.value = v.resources.getResourceName(v.id).split("/")[1]

    }

    /* val loading : LiveData<Boolean>
         get() = mIsUpdating

     fun clickListener(password : String, confirm : String) {
         if (password.isEmpty()) {
             mPassError.value =
                 MyApplication.instance.getString(R.string.empty) + " " + MyApplication.instance.getString(
                     R.string.enter_new_password1
                 )

         } else if (password.length < 8) {
             mPassError.value = MyApplication.instance.getString(R.string.password_len_msg)

         }
 //        } else if (!ValidationsClass.isValidPassword(password)) {
 //            passwordView!!.showPasswordError(MyApplication.instance.getString(R.string.password_validation))
 //        }
 //
         else if (confirm.isEmpty()) {
             mConfirmError.value =
                 MyApplication.instance.getString(R.string.empty) + " " + MyApplication.instance.getString(
                     R.string.confirm_password
                 )

         } else if (confirm.length < 8) {
             mConfirmError.value = MyApplication.instance.getString(R.string.password_len_msg)

         } else if (password != confirm) {
             mConfirmError.value = MyApplication.instance.getString(R.string.mismatch_paaword)

         } else {
             val mJsonObject = JsonObject()
             mJsonObject.addProperty("password", password)
             mJsonObject.addProperty(
                 "email",
                 sharedPrefClass.getPrefValue(
                     MyApplication.instance,
                     "user_email"
                 ) as String
             )

             if (UtilsFunctions.isNetworkConnected()) {
                 data = resetPasswordRepository.getResetPasswordResponse(mJsonObject)
                 mIsUpdating.postValue(true)

             }
         }

     }*/
    fun callResetPassword(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = resetPasswordRepository.getResetPasswordResponse(mJsonObject)
            mIsUpdating.postValue(true)

        }

    }

}