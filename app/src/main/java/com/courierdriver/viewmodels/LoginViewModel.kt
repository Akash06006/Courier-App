package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.model.CommonModel
import com.courierdriver.model.LoginResponse
import com.courierdriver.repositories.LoginRepository
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.Utils
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap

class LoginViewModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private var loginResponse: MutableLiveData<LoginResponse>? = null
    private var signupResponse: MutableLiveData<LoginResponse>? = null
    private var verifyUserResponse: MutableLiveData<CommonModel>? = null
    private var loginRepository = LoginRepository()
    private var sharedPrefClass = SharedPrefClass()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val postEmailError = MutableLiveData<String>()
    private val postPassError = MutableLiveData<String>()
    private var checkForSocialList: MutableLiveData<LoginResponse>? = MutableLiveData()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            loginResponse = loginRepository.getLoginData(null)
            signupResponse = loginRepository.callSignupApi(null, null)
            verifyUserResponse = loginRepository.callVerifyUserApi(null)
            checkForSocialList = loginRepository.checkSocial(null, checkForSocialList)
        }
    }

    fun getLoginRes(): LiveData<LoginResponse> {
        return loginResponse!!
    }

    fun getVerifyUserRes(): LiveData<CommonModel> {
        return verifyUserResponse!!
    }

    fun getSignupRes(): LiveData<LoginResponse> {
        return signupResponse!!
    }

    fun getEmailError(): LiveData<String> {
        return postEmailError
    }

    fun getPasswordError(): LiveData<String> {
        return postPassError
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    /* override fun isClick() : LiveData<String> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }
    override fun clickListener(v : View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }*/
    override fun isClick(): LiveData<String> {
        return isClick
    }

    override fun clickListener(v: View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun callLoginApi(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            loginResponse = loginRepository.getLoginData(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun callSignupApi(
        mJsonObject: HashMap<String, RequestBody>,
        userImage: MultipartBody.Part?
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            signupResponse = loginRepository.callSignupApi(mJsonObject, userImage)
            mIsUpdating.postValue(true)
        }

    }

    fun callVerifyUserApi(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            verifyUserResponse = loginRepository.callVerifyUserApi(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun checkForSocial(socialId: String?, email: String?, deviceToken: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            val mHashMap = HashMap<String, RequestBody>()
            mHashMap["email"] =
                Utils(MyApplication.instance).createPartFromString(email!!)
            mHashMap["socialId"] =
                Utils(MyApplication.instance).createPartFromString(socialId!!)
            mHashMap["deviceToken"] =
                Utils(MyApplication.instance).createPartFromString(GlobalConstants.NOTIFICATION_TOKEN)
            checkForSocialList = loginRepository.checkSocial(mHashMap, checkForSocialList)
            mIsUpdating.postValue(true)
        }
    }

    fun checkForSocialData(): LiveData<LoginResponse> {
        return checkForSocialList!!
    }
}