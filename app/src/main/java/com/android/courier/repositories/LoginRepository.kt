package com.android.courier.repositories

import androidx.lifecycle.MutableLiveData
import com.android.courier.R
import com.android.courier.api.ApiClient
import com.android.courier.api.ApiResponse
import com.android.courier.api.ApiService
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Response

class LoginRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private var signupData : MutableLiveData<LoginResponse>? = null
    private var data1 : MutableLiveData<CommonModel>? = null
    private var verifyUser : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        signupData = MutableLiveData()
        verifyUser = MutableLiveData()

    }

    fun getLoginData(jsonObject : JsonObject?) : MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    private var loginResponse : Any? = null

                    override fun onResponse(mResponse : Response<JsonObject>) {
                        loginResponse = if (mResponse.body() != null)
                            gson.fromJson<LoginResponse>(
                                "" + mResponse.body(),
                                LoginResponse::class.java
                            )
                        else {
                            //  if (mResponse.errorBody() != null) {
                            gson.fromJson<LoginResponse>(
                                mResponse.errorBody()!!.charStream(),
                                LoginResponse::class.java
                            )
                            /*  } else {
                                 loginResponse = null
                             }*/

                        }

                        if (loginResponse != null) {
                            data!!.postValue(loginResponse as LoginResponse?)
                        }

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogin(jsonObject)
            )

        }
        return data!!

    }

    fun callSignupApi(jsonObject : JsonObject?) : MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<LoginResponse>(
                                "" + mResponse.body(),
                                LoginResponse::class.java
                            )
                        else {
                            gson.fromJson<LoginResponse>(
                                mResponse.errorBody()!!.charStream(),
                                LoginResponse::class.java
                            )
                        }


                        signupData!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        signupData!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callSignup(jsonObject)
            )

        }
        return signupData!!

    }

    fun callVerifyUserApi(jsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }

                        verifyUser!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        verifyUser!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callVerifyUser(jsonObject)
            )

        }
        return verifyUser!!

    }

    fun getLogoutResonse(jsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val logoutResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }

                        data1!!.postValue(logoutResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogout(/*jsonObject*/)
            )

        }
        return data1!!
    }

    fun checkSocial(
        mJsonObject : HashMap<String, RequestBody>?,
        profileDetails : MutableLiveData<LoginResponse>?
    ): MutableLiveData<LoginResponse>? {

        if (UtilsFunctions.isNetworkConnected() && mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<LoginResponse>(
                            "" + mResponse.body()!!,
                            LoginResponse::class.java
                        )
                        profileDetails!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        profileDetails!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().checkSocial(mJsonObject)
            )
        }

        return profileDetails
    }
}