package com.courierdriver.views.home

import android.content.Intent
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.databinding.ActivityDefineWorkBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.DefineWorkModel
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.DefineWorkViewModel
import com.courierdriver.views.authentication.DocumentVerificatonActivity
import com.google.gson.JsonObject


class DefineWorkActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener {
    private var binding: ActivityDefineWorkBinding? = null
    private var viewModel: DefineWorkViewModel? = null
    private var pageCount = 1
    private var tutorialQuesData: DefineWorkModel.Body? = null
    private var selectedOption = ""

    override fun initViews() {
        binding = viewDataBinding as ActivityDefineWorkBinding?
        viewModel = ViewModelProviders.of(this).get(DefineWorkViewModel::class.java)
        binding!!.viewModel = viewModel

        loaderObserver()
        viewClicks()
        binding!!.radioGroup.setOnCheckedChangeListener(this)
        viewModel!!.tutorialQuestions(pageCount)
        tutorialQuestionsObserver()
        saveTutorialQuesObserver()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_skip" -> {
                        if (pageCount < 3) {
                            pageCount += 1
                            viewModel!!.tutorialQuestions(pageCount)
                        } else {
                            val intent = Intent(this, DocumentVerificatonActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                    "tv_next" -> {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("queId", tutorialQuesData!!.id)
                        jsonObject.addProperty("option", selectedOption)
                        viewModel!!.saveTutorialQuestions(jsonObject)
                    }
                }
            })
        )
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rb_option1 -> {
                selectedOption = tutorialQuesData!!.option1!!
            }
            R.id.rb_option2 -> {
                selectedOption = tutorialQuesData!!.option2!!
            }
            R.id.rb_option3 -> {
                selectedOption = tutorialQuesData!!.option3!!
            }
            R.id.rb_option4 -> {
                selectedOption = tutorialQuesData!!.option4!!
            }
        }
    }

    private fun tutorialQuestionsObserver() {
        viewModel!!.tutorialQuestionsData().observe(this,
            Observer<DefineWorkModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(response.message!!)
                            tutorialQuesData = response.body!![0]
                            binding!!.model = tutorialQuesData
                            selectedOption = tutorialQuesData!!.option1!!
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun saveTutorialQuesObserver() {
        viewModel!!.saveTutorialQuesData().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(response.message!!)
                            selectedOption = ""
                            if (pageCount < 3) {
                                pageCount += 1
                                viewModel!!.tutorialQuestions(pageCount)
                            } else {
                                val intent = Intent(this, DocumentVerificatonActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_define_work
    }
}