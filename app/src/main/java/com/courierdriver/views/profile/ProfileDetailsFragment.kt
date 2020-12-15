package com.courierdriver.views.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.common.UtilsFunctions.showToastError
import com.courierdriver.common.UtilsFunctions.showToastSuccess
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityProfileDetailsBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.profile.ProfileDetailsModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DialogssInterface
import com.courierdriver.utils.Utils
import com.courierdriver.viewmodels.profile.ProfileViewModel
import com.courierdriver.views.authentication.LoginActivity
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
    private var todaySelfieImage = ""
    private var notificationToken = ""

    override fun initView() {
        binding = viewDataBinding as ActivityProfileDetailsBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.viewModel = viewModel

        viewModel!!.profileDetails("1")
        sharedPrefValue()
        getProfileDetailsObserver()
        uploadSelfieObserver()
        loaderObserver()
        viewClicks()
    }

    private fun sharedPrefValue() {
        notificationToken =
            SharedPrefClass().getPrefValue(baseActivity, GlobalConstants.NOTIFICATION_TOKENPref)
                .toString()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(
                function =
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
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Please download app from ${GlobalConstants.TERMS_AND_CONDITIONS} Use my referral code to earn points ${model!!.referralCode}"
                                )
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
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(baseActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri =
                        FileProvider.getUriForFile(
                            baseActivity,
                            baseActivity.packageName + ".fileprovider",
                            it
                        )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = baseActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            todaySelfieImage = absolutePath
        }
    }

/*
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
*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            fileUri = todaySelfieImage
            uploadSelfieApi()
/*
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
*/
        } /*else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val resultUri = result.uri
                fileUri = RealPathUtil.getRealPath(baseActivity, resultUri)!!
                val file = File(fileUri)
                imageFile = file

                uploadSelfieApi()
            }
        }*/
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
        viewModel!!.uploadSelfie(userImage, "today selfie", "", "")
    }

    private fun uploadSelfieObserver() {
        viewModel!!.uploadSelfieListData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (confirmationDialog != null)
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
                            SharedPrefClass().clearAll(baseActivity)
                            SharedPrefClass().putObject(
                                baseActivity,
                                GlobalConstants.NOTIFICATION_TOKENPref,
                                notificationToken
                            )
                            GlobalConstants.NOTIFICATION_TOKEN = notificationToken

                            val intent1 = Intent(baseActivity, LoginActivity::class.java)
                            intent1.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent1)
                            baseActivity.finish()
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

                            if (response.body!!.isApproved!!) {
                                binding!!.tvApproved.text =
                                    getString(R.string.approved_by_system_administrator)
                                binding!!.tvApproved.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_tick,
                                    0,
                                    0,
                                    0
                                )
                            } else {
                                binding!!.tvApproved.text =
                                    getString(R.string.approval_inprogress)
                                binding!!.tvApproved.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_help_red,
                                    0,
                                    0,
                                    0
                                )
                            }
/*
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
*/

                            if (response.body.totalOrders!!.toInt() > 0) {
                                binding!!.tvNoCompOrders.text =
                                    response.body.totalOrders + " " + getString(R.string.completed_orders)
                                binding!!.tvNoCompOrders.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_tick,
                                    0,
                                    0,
                                    0
                                )
                            } else {
                                binding!!.tvNoCompOrders.text =
                                    getString(R.string.no_completed_orders)
                                binding!!.tvNoCompOrders.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_help_red,
                                    0,
                                    0,
                                    0
                                )
                            }
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