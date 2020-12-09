package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.courierdriver.model.GetSubjectsModel
import com.courierdriver.repositories.HelpRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class SpotAndErrorViewModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private var helpRepository: HelpRepository = HelpRepository()
    private var subjectList: MutableLiveData<GetSubjectsModel>? = MutableLiveData()
    private var addComplaintList: MutableLiveData<CommonModel>? = MutableLiveData()

    init {
        addComplaintList = helpRepository.addComplaints(null, null, addComplaintList)
    }

    fun subjectList() {
        if (UtilsFunctions.isNetworkConnected()) {
            subjectList = helpRepository.getSubjects(subjectList)
            mIsUpdating.postValue(true)
        }
    }

    fun subjectListData(): LiveData<GetSubjectsModel> {
        return subjectList!!
    }


    fun addComplaints(
        image: MultipartBody.Part?, bodyHashMap: HashMap<String, RequestBody>
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            addComplaintList =
                helpRepository.addComplaints(bodyHashMap, image!!, addComplaintList)
            mIsUpdating.postValue(true)
        }
    }

    fun addComplaintListData(): LiveData<CommonModel> {
        return addComplaintList!!
    }


    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return isClick
    }

    override fun clickListener(v: View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }
}