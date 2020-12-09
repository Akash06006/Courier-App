package com.courierdriver.viewmodels.profile

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.*
import com.courierdriver.model.profile.AccountDetailsModel
import com.courierdriver.model.profile.ProfileDetailsModel
import com.courierdriver.repositories.HelpRepository
import com.courierdriver.repositories.profile.ProfileRepository
import com.courierdriver.viewmodels.BaseViewModel
import com.example.services.repositories.home.HomeRepository
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class ProfileViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var profileRepository = ProfileRepository()
    private var homeRepository = HomeRepository()
    private var helpRepository: HelpRepository = HelpRepository()
    private var getProfileDetails: MutableLiveData<ProfileDetailsModel>? = MutableLiveData()
    private var accountDetails: MutableLiveData<AccountDetailsModel>? = MutableLiveData()
    private var statisticsData: MutableLiveData<StatisticsModel>? = MutableLiveData()
    private var locomoIdData: MutableLiveData<LocomoIdModel>? = MutableLiveData()
    private var regionList: MutableLiveData<RegionListModel>? = MutableLiveData()
    private var vehicleList: MutableLiveData<GetVehiclesModel>? = MutableLiveData()
    private var updateProfile: MutableLiveData<CommonModel>? = MutableLiveData()
    private var logoutList: MutableLiveData<CommonModel>? = MutableLiveData()
    private var profileSetupList: MutableLiveData<CommonModel>? = MutableLiveData()
    private var convertPointsList: MutableLiveData<CommonModel>? = MutableLiveData()
    private var paymentOptionsList: MutableLiveData<PaymentOptionsModel>? = MutableLiveData()
    private var uploadSelfieList: MutableLiveData<CommonModel>? = MutableLiveData()
    private var payComissionList: MutableLiveData<CommonModel>? = MutableLiveData()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            getProfileDetails = profileRepository.getProfileDetails(null, getProfileDetails)
            accountDetails = profileRepository.accountDetails(null, accountDetails)
            statisticsData =
                profileRepository.statisticsData(null, null, null, null, statisticsData)
            locomoIdData = profileRepository.locomoIdData(null, locomoIdData)
            updateProfile = profileRepository.updateProfile(null, null, updateProfile)
            // logoutList = helpRepository.logout(logoutList)
            profileSetupList = homeRepository.profileSetup(null, profileSetupList)
            paymentOptionsList = homeRepository.paymentOptions(paymentOptionsList)
            uploadSelfieList =
                profileRepository.uploadSelfie(null, null, null, null, uploadSelfieList)
            payComissionList = profileRepository.payComission(null, payComissionList)
        }
    }

    fun convertPoints() {
        if (UtilsFunctions.isNetworkConnected()) {
            convertPointsList = profileRepository.convertPoints(convertPointsList)
            mIsUpdating.postValue(true)
        }
    }

    fun convertPointsData(): LiveData<CommonModel> {
        return convertPointsList!!
    }

    fun paymentOptions() {
        if (UtilsFunctions.isNetworkConnected()) {
            paymentOptionsList = homeRepository.paymentOptions(paymentOptionsList)
            mIsUpdating.postValue(true)
        }
    }

    fun paymentOptionsData(): LiveData<PaymentOptionsModel> {
        return paymentOptionsList!!
    }

    fun profileSetup(
        gPayNo: String?,
        phonePayNo: String?,
        paytmNo: String?,
        paymentType: String
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("paymentType", paymentType)
            jsonObject.addProperty("gpayNo", gPayNo)
            jsonObject.addProperty("phonePayNo", phonePayNo)
            jsonObject.addProperty("paytmNo", paytmNo)
            profileSetupList = homeRepository.profileSetup(jsonObject, profileSetupList)
            mIsUpdating.postValue(true)
        }
    }

    fun profileSetupListData(): LiveData<CommonModel> {
        return profileSetupList!!
    }

    fun payComission(transactionId: String?, amount: String?) {
        if (UtilsFunctions.isNetworkConnected()) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("transactionId", transactionId)
            jsonObject.addProperty("amount", amount)
//            jsonObject.addProperty("usedPoints","")
            payComissionList = profileRepository.payComission(jsonObject, payComissionList)
            mIsUpdating.postValue(true)
        }
    }

    fun payComissionData(): LiveData<CommonModel> {
        return payComissionList!!
    }

    fun logout() {
        if (UtilsFunctions.isNetworkConnected()) {
            logoutList = helpRepository.logout(logoutList)
            mIsUpdating.postValue(true)
        }
    }

    fun logoutData(): LiveData<CommonModel> {
        return logoutList!!
    }

    fun regionList() {
        if (UtilsFunctions.isNetworkConnected()) {
            regionList = homeRepository.regionList(regionList)
            mIsUpdating.postValue(true)
        }
    }

    fun regionListData(): LiveData<RegionListModel> {
        return regionList!!
    }

    fun transporterList() {
        if (UtilsFunctions.isNetworkConnected()) {
            vehicleList = profileRepository.getVehicles(vehicleList)
            mIsUpdating.postValue(true)
        }
    }

    fun transporterListData(): LiveData<GetVehiclesModel> {
        return vehicleList!!
    }

    fun profileDetails(tab: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            getProfileDetails = profileRepository.getProfileDetails(tab, getProfileDetails)
            mIsUpdating.postValue(true)
        }
    }

    fun profileDetailData(): LiveData<ProfileDetailsModel> {
        return getProfileDetails!!
    }

    fun accountDetails(tab: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            accountDetails = profileRepository.accountDetails(tab, accountDetails)
            mIsUpdating.postValue(true)
        }
    }

    fun accountDetailsData(): LiveData<AccountDetailsModel> {
        return accountDetails!!
    }

    fun statistics(
        tab: String, year: String?,
        month: String?, week: String?
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            statisticsData =
                profileRepository.statisticsData(tab, year, month, week, statisticsData)
            mIsUpdating.postValue(true)
        }
    }

    fun statisticsData(): LiveData<StatisticsModel> {
        return statisticsData!!
    }

    fun locomoId(tab: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            locomoIdData = profileRepository.locomoIdData(tab, locomoIdData)
            mIsUpdating.postValue(true)
        }
    }

    fun locomoIdGetData(): LiveData<LocomoIdModel> {
        return locomoIdData!!
    }

    fun updateProfile(mHashMap: HashMap<String, RequestBody>, userImage: MultipartBody.Part?) {
        updateProfile = profileRepository.updateProfile(mHashMap, userImage, updateProfile)
        mIsUpdating.postValue(true)
    }

    fun updateProfileData(): LiveData<CommonModel> {
        return updateProfile!!
    }

    fun uploadSelfie(
        userImage: MultipartBody.Part?,
        type: String?,
        orderId: String?,
        addressId: String?
    ) {
        uploadSelfieList =
            profileRepository.uploadSelfie(userImage, type, orderId, addressId, uploadSelfieList)
        mIsUpdating.postValue(true)
    }

    fun uploadSelfieListData(): LiveData<CommonModel> {
        return uploadSelfieList!!
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
}