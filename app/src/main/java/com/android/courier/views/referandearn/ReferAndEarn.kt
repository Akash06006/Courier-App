package com.android.courier.views.refer

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.bumptech.glide.Glide
import com.android.courier.application.MyApplication
import com.android.courier.constants.GlobalConstants
import com.android.courier.model.CommonModel
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogClass
import com.android.courier.utils.DialogssInterface
import com.android.courier.viewmodels.notifications.NotificaionViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import com.android.courier.databinding.ActivityReferEarnBinding
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReferAndEarn : BaseActivity(), DialogssInterface {
    lateinit var reviewsBinding : ActivityReferEarnBinding
    lateinit var reviewsViewModel : NotificaionViewModel
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    override fun getLayoutId() : Int {
        return R.layout.activity_refer_earn
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "Rating" -> {
                confirmationDialog?.dismiss()
                finish()
            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        when (mKey) {
            "Rating" -> confirmationDialog?.dismiss()
        }
    }

    override fun initViews() {
        reviewsBinding = viewDataBinding as ActivityReferEarnBinding
        reviewsViewModel = ViewModelProviders.of(this).get(NotificaionViewModel::class.java)
        reviewsBinding.commonToolBar.imgRight.visibility = View.GONE
        val referralCode = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.REFERRAL_CODE
        ).toString()
        val userImage =
            SharedPrefClass().getPrefValue(this, GlobalConstants.USER_IMAGE).toString()
        Glide.with(this).load(userImage).placeholder(R.drawable.ic_user)
        reviewsBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.refer_earn)
        reviewsBinding.reviewsViewModel = reviewsViewModel
        reviewsBinding.txtMessage.setText(
            "This referral code gives " + GlobalConstants.REFERRAL_POINT_GIVE + " loyalty points to your friends when they will signup in " + resources.getString(
                R.string.app_name
            ) + " using this code, and you will receive " + GlobalConstants.REFERRAL_POINT_EARN + " loyalty points for a referral."
        )
        reviewsBinding.txtCode.setText(referralCode)
        //UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        reviewsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgBack" -> {
                        onBackPressed()
                    }
                    "btnSubmit" -> {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Locomo App")
                            var shareMessage =
                                "Hey!! join me on Locomo app and get some exciting offers. Use this referral code $referralCode for login/signup\n\n Android Play Store:\n " + GlobalConstants.ANDROID_LINK + "\n\niPhone App Store:\n" + GlobalConstants.ANDROID_LINK
                            shareMessage = shareMessage
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "choose one"))
                        } catch (e : Exception) {
                            //e.toString();
                        }
                    }
                }
            })
        )

    }

}
