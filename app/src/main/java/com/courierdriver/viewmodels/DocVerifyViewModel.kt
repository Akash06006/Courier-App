package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.*
import com.courierdriver.model.profile.RegionResponse
import com.courierdriver.repositories.DocVerifyRepository
import com.courierdriver.repositories.profile.ProfileRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DocVerifyViewModel : BaseViewModel() {
    private var regionResponse = MutableLiveData<RegionResponse>()
    private var data = MutableLiveData<LoginResponse>()
    private var uploadDocNewData = MutableLiveData<LoginResponse>()
    private var addDocDataList = MutableLiveData<AddDocModel>()
    private var docVerifyRepository = DocVerifyRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var profileRepository = ProfileRepository()
    private var documentDetails: MutableLiveData<ProfileDocumentModel>? = MutableLiveData()
    private var vehicleList: MutableLiveData<GetVehiclesModel>? = MutableLiveData()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            data = docVerifyRepository.docVerify(null, null, null, null, null, null)
            addDocDataList = docVerifyRepository.addDoc(null,addDocDataList)
            documentDetails = profileRepository.documentsData(null, documentDetails)
            uploadDocNewData  = docVerifyRepository.uploadDocNew(null,uploadDocNewData)
        }
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

    fun documentDetails(tab: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            documentDetails = profileRepository.documentsData(tab,documentDetails)
            mIsUpdating.postValue(true)
        }
    }

    fun documentDetailsData(): LiveData<ProfileDocumentModel> {
        return documentDetails!!
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

    fun getDocVerify(): LiveData<LoginResponse> {
        return data
    }

    fun addDoc(docImage: MultipartBody.Part?
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            addDocDataList = docVerifyRepository.addDoc(
                docImage,
                addDocDataList
            )
            mIsUpdating.postValue(true)
        }
    }

    fun addDocData(): LiveData<AddDocModel> {
        return addDocDataList
    }

    fun uploadDocNew(mHashMap: HashMap<String, RequestBody>) {

        if(UtilsFunctions.isNetworkConnected())
        {
            uploadDocNewData  = docVerifyRepository.uploadDocNew(mHashMap,uploadDocNewData)
            mIsUpdating.postValue(true)
        }
    }

    fun uploadDocNewDataList(): LiveData<LoginResponse> {
        return uploadDocNewData
    }

    /*fun getRegions(hashMap : HashMap<String, RequestBody>, image : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = docVerifyRepository.getRegoins()
            mIsUpdating.postValue(true)

        }
    }*/

}