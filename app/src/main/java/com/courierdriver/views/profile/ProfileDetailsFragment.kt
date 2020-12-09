package com.courierdriver.views.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.common.UtilsFunctions.showToastError
import com.courierdriver.common.UtilsFunctions.showToastSuccess
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityProfileDetailsBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.profile.ProfileDetailsModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.*
import com.courierdriver.viewmodels.profile.ProfileViewModel
import com.courierdriver.views.authentication.LoginActivity
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileDetailsFragment : BaseFragment(), DialogssInterface, SelfieCallBack {
    private var binding: ActivityProfileDetailsBinding? = null
    private var viewModel: ProfileViewModel? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private var selfieDialog: Dialog? = null
    private var photoFile: File? = null
    private val CAMERA_REQUEST = 1808
    private var fileUri = ""
    private var imageFile: File? = null
    private var model: ProfileDetailsModel.Body? = null

    override fun initView() {
        binding = viewDataBinding as ActivityProfileDetailsBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.viewModel = viewModel

        viewModel!!.profileDetails("1")
        getProfileDetailsObserver()
        uploadSelfieObserver()
        loaderObserver()
        viewClicks()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_sign_out" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            baseActivity,
                            this,
                            "logout",
                            getString(R.string.logout),
                            getString(R.string.want_to_logout),
                            getString(R.string.logout_alert)
                        )
                        confirmationDialog!!.show()
                    }
                    "tv_invite_friend" -> {
                        val shareIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Please download app from ${GlobalConstants.TERMS_AND_CONDITIONS} Use my referral code to earn points ${model!!.referralCode}")
                            type = "text/plain"
                        }
                        startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Send to"
                            )
                        )
                    }
                    "tv_selfie" -> {
                        showTakeSelfieAlert()
                    }
                }
            })
        )
    }

    private fun showTakeSelfieAlert() {
        if (baseActivity.checkAndRequestPermissions()) {
            selfieDialog = mDialogClass.setUploadSelfieConfirmationDialog(
                baseActivity,
                this, ""
            )
        }
    }

    override fun selfieFromCamera(mKey: String) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(baseActivity.packageManager) == null) {
            return
        }
        val fileName = "CAMERA_" + "img" + ".jpg"
        //val photoFile = getTemporaryCameraFile(fileName)
        photoFile = File(baseActivity.externalCacheDir, fileName)
        val uri = UtilsFunctions.getValidUri(photoFile!!, baseActivity)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {

                CropImage.activity(data.data)
                    .start(baseActivity);
            } else {
                if (photoFile != null) {
                    val data1 = Intent()
                    data1.data = UtilsFunctions.getValidUri(photoFile!!, baseActivity)
                    CropImage.activity(data1.data)
                        .start(baseActivity);
                }
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val resultUri = result.uri
                fileUri = RealPathUtil.getRealPath(baseActivity, resultUri)!!
                val file = File(fileUri)
                imageFile = file

                uploadSelfieApi()
            }
        }
    }

    private fun uploadSelfieApi() {
        /*val mHashMap = HashMap<String, RequestBody>()
        mHashMap["type"] =
            Utils(activity!!).createPartFromString("today selfie")*/
        var userImage: MultipartBody.Part? = null
        if (fileUri.isNotEmpty()) {
            val f1 = File(fileUri)
            userImage =
                Utils(activity!!).prepareFilePart(
                    "image",
                    f1
                )
        }
        viewModel!!.uploadSelfie(userImage,"today selfie","","")
    }

    private fun uploadSelfieObserver() {
        viewModel!!.uploadSelfieListData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            confirmationDialog!!.dismiss()
                            showToastSuccess(response.message!!)
                        }
                        else -> {
                            showToastError(response.message!!)
                        }
                    }
                } else {
                    showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun logoutObserver() {
        viewModel!!.logout()
        viewModel!!.logoutData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            confirmationDialog!!.dismiss()
                            showToastSuccess(response.message!!)
                            SharedPrefClass().putObject(
                                baseActivity,
                                "isLogin",
                                false
                            )
                            val intent1 = Intent(baseActivity, LoginActivity::class.java)
                            startActivity(intent1)
                            baseActivity.finish()
                            SharedPrefClass().clearAll(baseActivity)
                        }
                        else -> {
                            showToastError(response.message!!)
                        }
                    }
                } else {
                    showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String?) {
        logoutObserver()
    }

    override fun onDialogCancelAction(mView: View?, mKey: String?) {
        confirmationDialog!!.dismiss()
    }

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    private fun getProfileDetailsObserver() {
        viewModel!!.profileDetailData().observe(this,
            Observer<ProfileDetailsModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            //  showToastSuccess(response.body!!.userName!!)
                            binding!!.model = response.body
                            model = response.body

                            when (response.body!!.status) {
                                "0" -> //Pending
                                {
                                    binding!!.tvApproved.text =
                                        getString(R.string.approval_inprogress)
                                    binding!!.tvApproved.setCompoundDrawablesWithIntrinsicBounds(
                                        R.drawable.ic_warning,
                                        0,
                                        0,
                                        0
                                    )
                                }
                                "1" -> //Approved
                                {
                                    binding!!.tvApproved.text =
                                        getString(R.string.approved_by_system_administrator)
                                    binding!!.tvApproved.setCompoundDrawablesWithIntrinsicBounds(
                                        R.drawable.ic_tick,
                                        0,
                                        0,
                                        0
                                    )
                                }
                                "2" -> //Blocked
                                {
                                    binding!!.tvApproved.text =
                                        getString(R.string.blocked_by_system_administrator)
                                    binding!!.tvApproved.setCompoundDrawablesWithIntrinsicBounds(
                                        R.drawable.ic__cross_round,
                                        0,
                                        0,
                                        0
                                    )
                                }
                            }

                            if (response.body.noCompletedOrds!!)
                                binding!!.linNoCompOrders.visibility = View.VISIBLE
                            else
                                binding!!.linNoCompOrders.visibility = View.GONE
                        }
                        else -> {
                            showToastError(response.message!!)
                        }
                    }
                } else {
                    showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_profile_details
    }
}