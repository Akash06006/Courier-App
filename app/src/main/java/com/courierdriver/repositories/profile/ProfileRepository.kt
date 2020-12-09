package com.courierdriver.repositories.profile

import androidx.lifecycle.MutableLiveData
import com.courierdriver.R
import com.courierdriver.api.ApiClient
import com.courierdriver.api.ApiResponse
import com.courierdriver.api.ApiService
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.*
import com.courierdriver.model.profile.AccountDetailsModel
import com.courierdriver.model.profile.ProfileDetailsModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.*

class ProfileRepository {
    private val gson = GsonBuilder().serializeNulls().create()


    fun getProfileDetails(
        tab: String?,
        profileDetails: MutableLiveData<ProfileDetailsModel>?
    ): MutableLiveData<ProfileDetailsModel>? {

        if (UtilsFunctions.isNetworkConnected() && tab != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<ProfileDetailsModel>(
                            "" + mResponse.body()!!,
                            ProfileDetailsModel::class.java
                        )
                        profileDetails!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        profileDetails!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().profileDetails(tab, "", "", "")
            )
        }

        return profileDetails
    }


    fun accountDetails(
        tab: String?,
        accountDetails: MutableLiveData<AccountDetailsModel>?
    ): MutableLiveData<AccountDetailsModel>? {
        if (UtilsFunctions.isNetworkConnected() && tab != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<AccountDetailsModel>(
                            "" + mResponse.body()!!,
                            AccountDetailsModel::class.java
                        )
                        accountDetails!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        accountDetails!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().profileDetails(tab, "", "", "")
            )
        }

        return accountDetails
    }

    fun statisticsData(
        tab: String?, year: String?,
        month: String?, week: String?,
        statisticsData: MutableLiveData<StatisticsModel>?
    ): MutableLiveData<StatisticsModel>? {
        if (UtilsFunctions.isNetworkConnected() && tab != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<StatisticsModel>(
                            "" + mResponse.body()!!,
                            StatisticsModel::class.java
                        )
                        statisticsData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        statisticsData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().profileDetails(tab, year!!, month!!, week!!)
            )
        }

        return statisticsData
    }

    fun locomoIdData(
        tab: String?,
        statisticsData: MutableLiveData<LocomoIdModel>?
    ): MutableLiveData<LocomoIdModel>? {
        if (UtilsFunctions.isNetworkConnected() && tab != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        if (mResponse.body() != null) {
                            val data = gson.fromJson<LocomoIdModel>(
                                "" + mResponse.body()!!,
                                LocomoIdModel::class.java
                            )
                            statisticsData!!.postValue(data)
                        } else
                            statisticsData!!.postValue(null)
                    }

                    override fun onError(mKey: String) {
                        statisticsData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().profileDetails(tab, "", "", "")
            )
        }

        return statisticsData
    }

    fun documentsData(
        tab: String?,
        statisticsData: MutableLiveData<ProfileDocumentModel>?
    ): MutableLiveData<ProfileDocumentModel>? {
        if (UtilsFunctions.isNetworkConnected() && tab != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<ProfileDocumentModel>(
                            "" + mResponse.body()!!,
                            ProfileDocumentModel::class.java
                        )
                        statisticsData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        statisticsData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().profileDetails(tab, "", "", "")
            )
        }

        return statisticsData
    }

    fun getVehicles(
        vehiclesData: MutableLiveData<GetVehiclesModel>?
    ): MutableLiveData<GetVehiclesModel>? {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<GetVehiclesModel>(
                            "" + mResponse.body()!!,
                            GetVehiclesModel::class.java
                        )
                        vehiclesData!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        vehiclesData!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().getVehicleList()
            )
        }
        return vehiclesData
    }

    fun updateProfile(
        mHashMap: HashMap<String, RequestBody>?,
        userImage: MultipartBody.Part?,
        updateProfile: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel>? {
        if (UtilsFunctions.isNetworkConnected() && mHashMap != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        updateProfile!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        updateProfile!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().updateProfile(mHashMap, userImage)
            )
        }
        return updateProfile
    }

    fun convertPoints(
        updateProfile: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel>? {
        if (UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        updateProfile!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        updateProfile!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().convertPoints()
            )
        }
        return updateProfile
    }


    fun uploadSelfie(
        userImage: MultipartBody.Part?,
        type: String?,
        orderId: String?,
        addressId: String?,
        uploadSelfieList: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel>? {
        if (UtilsFunctions.isNetworkConnected() && orderId != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val data = gson.fromJson<CommonModel>(
                            "" + mResponse.body()!!,
                            CommonModel::class.java
                        )
                        uploadSelfieList!!.postValue(data)
                    }

                    override fun onError(mKey: String) {
                        uploadSelfieList!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().uploadSelfie(userImage,type!!,orderId,addressId!!)
            )
        }
        return uploadSelfieList
    }


    fun payComission(
        jsonObject: JsonObject?,
        responseData: MutableLiveData<CommonModel>?
    ): MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
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
                        responseData!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        responseData!!.postValue(null)
                    }
                }, ApiClient.getApiInterface().payComission(jsonObject)
            )
        }
        return responseData!!
    }
}