package com.android.courier.views.contactus

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.courier.R
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityContactUsBinding
import com.android.courier.model.CommonModel
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.viewmodels.contactus.ContactUsViewModel
import com.google.gson.JsonObject

class ContactUsActivity : BaseActivity() {
    lateinit var binding : ActivityContactUsBinding
    private var contactUsViewModel : ContactUsViewModel? = null

    override fun getLayoutId() : Int {
        return R.layout.activity_contact_us
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityContactUsBinding
        contactUsViewModel = ViewModelProviders.of(this).get(ContactUsViewModel::class.java)
        binding.contactUsViewModel = contactUsViewModel
        // binding.toolbarCommon.imgToolbarText.text = getString(R.string.contact_us)
        val email = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USEREMAIL
        )
        binding.txtCall.text = GlobalConstants.ADMIN_MOB_NUMBER
        contactUsViewModel!!.addConcernRes().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            binding.edtQuery.setText("")
                            showToastSuccess(response.message)
                            finish()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(message)
                            // faqListBinding.btnClear.visibility = View.GONE
                        }
                    }

                }
            })
        contactUsViewModel!!.isClick().observe(
            this, Observer<String>(
                function =
                fun(it : String?) {
                    when (it) {
                        "imgBack" -> {
                            finish()
                        }
                        "txtCall" -> {
                            // finish()
                            /*   val intent = Intent(
                                   Intent.ACTION_CALL,
                                   Uri.parse("tel:" + binding.txtCall.getText().toString())
                               )
                               startActivity(intent)*/
                            val number = binding.txtCall.getText().toString()
                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:" + number)
                            startActivity(dialIntent)
                        }
                        "btnSubmit" -> {
                            // finish()
                            if (TextUtils.isEmpty(binding.edtQuery.getText().trim().toString())) {
                                showToastError("Please enter your query")
                            } else {
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty(
                                    "query", binding.edtQuery.getText().toString()
                                )
                                mJsonObject.addProperty(
                                    "email", email.toString()
                                )
                                contactUsViewModel!!.addConcern(mJsonObject)
                            }

                        }
                    }

                })
        )

    }
}
