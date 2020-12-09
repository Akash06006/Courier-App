package com.courierdriver.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.model.CommonModel
import com.courierdriver.model.DefineWorkModel
import com.courierdriver.repositories.HelpRepository
import com.google.gson.JsonObject

class DefineWorkViewModel : BaseViewModel() {
    private val isClick = MutableLiveData<String>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private var helpRepository: HelpRepository = HelpRepository()
    private var tutorialQuestionsList: MutableLiveData<DefineWorkModel>? = MutableLiveData()
    private var saveTutorialQuesList: MutableLiveData<CommonModel>? = MutableLiveData()

    init {
        tutorialQuestionsList = helpRepository.tutorialQuestions(null, tutorialQuestionsList)
        saveTutorialQuesList = helpRepository.saveTutorialQuestions(null, saveTutorialQuesList)
    }

    fun tutorialQuestions(page: Int?) {
        if (UtilsFunctions.isNetworkConnected()) {
            tutorialQuestionsList = helpRepository.tutorialQuestions(page, tutorialQuestionsList)
            mIsUpdating.postValue(true)
        }
    }

    fun tutorialQuestionsData(): LiveData<DefineWorkModel> {
        return tutorialQuestionsList!!
    }

    fun saveTutorialQuestions(jsonObject: JsonObject?) {
        if (UtilsFunctions.isNetworkConnected()) {
            saveTutorialQuesList = helpRepository.saveTutorialQuestions(jsonObject!!, saveTutorialQuesList)
            mIsUpdating.postValue(true)
        }
    }

    fun saveTutorialQuesData(): LiveData<CommonModel> {
        return saveTutorialQuesList!!
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

    val loading: LiveData<Boolean>
        get() = mIsUpdating
}