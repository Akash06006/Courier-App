package com.courierdriver.repositories

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class HelpRepository {
    private val gson = GsonBuilder().serializeNulls().create()

    fun getSubjects(
        subjectsData: MutableLiveData<GetSubjectsModel>?
    ): MutableLiveData<GetSubjectsModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<GetSubjectsModel>(
                            "" + mResponse.body()!!,
                            GetSubjectsModel::class.java
                        )
                        subjectsData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        subjectsData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().getSubjects()
            )
        }
        return subjectsData!!
    }


    fun addComplaints(
        bodyHashMap: java.util.HashMap<String, RequestBody>?, image: MultipartBody.Part?,
        data: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected() && bodyHashMap != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val response = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(), CommonModel::class.java
                            )
                        }
                        data!!.postValue(response)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }
                }, ApiClient.getApiInterface().addComplaints(bodyHashMap, image)
            )
        }
        return data!!
    }

    fun logout(
        logoutData: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        logoutData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        logoutData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().logout()
            )
        }
        return logoutData!!
    }

    fun paymentHistory(
        paymentData: MutableLiveData<PaymentHistoryModel>?
    ): MutableLiveData<PaymentHistoryModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        if (mResponse.body() != null) {
                            val data = gson.fromJson<PaymentHistoryModel>(
                                "" + mResponse.body()!!,
                                PaymentHistoryModel::class.java
                            )
                            paymentData!!.postValue(data)
                        } else {
                            paymentData!!.postValue(null)
                        }
                    }

                    override fun onError(mKey: String) {
                        paymentData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().paymentHistory()
            )
        }
        return paymentData!!
    }

    fun getNotifications(
        notificationData: MutableLiveData<NotificationListModel>?
    ): MutableLiveData<NotificationListModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<NotificationListModel>(
                            "" + mResponse.body()!!,
                            NotificationListModel::class.java
                        )
                        notificationData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        notificationData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().notification()
            )
        }
        return notificationData!!
    }

    fun tutorialQuestions(
        page: Int?,
        workData: MutableLiveData<DefineWorkModel>?
    ): MutableLiveData<DefineWorkModel> {
        if (UtilsFunctions.isNetworkConnected() && page != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<DefineWorkModel>(
                            "" + mResponse.body()!!,
                            DefineWorkModel::class.java
                        )
                        workData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        workData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().tutorialQue(page!!)
            )
        }
        return workData!!
    }

    fun saveTutorialQuestions(
        jsonObject: JsonObject?,
        workData: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected() && jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        workData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        workData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().saveTutorialAns(jsonObject)
            )
        }
        return workData!!
    }
}