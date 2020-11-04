package com.courierdriver.views.authentication

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
import com.bumptech.glide.Glide
import com.courierdriver.R
import com.courierdriver.callbacks.ChoiceCallBack
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityDocumentVerificatonBinding
import com.courierdriver.model.LoginResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.Utils
import com.courierdriver.viewmodels.DocVerifyViewModel
import com.courierdriver.views.home.LandingActivty
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DocumentVerificatonActivity : BaseActivity(), ChoiceCallBack, SelfieCallBack {
    private lateinit var activityDocVeribinding: ActivityDocumentVerificatonBinding
    private lateinit var docVerifyViewModel: DocVerifyViewModel
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    private var buttonClickedId = ""
    private var aadharFrontImg = ""
    private var aadharBackImg = ""
    private var drivingBackImg = ""
    private var drivingFrontImg = ""
    private var panCardImage = ""
    private var dlNumber = ""
    private var transportType = ""
    var userId = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_document_verificaton
    }

    override fun initViews() {
        activityDocVeribinding = viewDataBinding as ActivityDocumentVerificatonBinding
        docVerifyViewModel = ViewModelProviders.of(this).get(DocVerifyViewModel::class.java)
        activityDocVeribinding.docVerifyViewModel = docVerifyViewModel
        activityDocVeribinding.toolbarCommon.imgToolbarText.text = "Document Information"
        activityDocVeribinding.toolbarCommon.imgRight.visibility = View.GONE
        userId =
            SharedPrefClass().getPrefValue(this, GlobalConstants.USER_ID).toString()


        docVerifyViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                buttonClickedId = it.toString()
                when (it) {
                    "btn_submit" -> {
                        dlNumber = activityDocVeribinding.etDrivingLicenseNo.text.toString()
                        dlNumber = activityDocVeribinding.etLastname.text.toString()
                        val mHashMap = HashMap<String, RequestBody>()
                        mHashMap["dlNumber"] =
                            Utils(this).createPartFromString(dlNumber)
                        mHashMap["transportType"] =
                            Utils(this).createPartFromString(transportType)
                        //  mHashMap["password"] = Utils(this).createPartFromString(password)
                        var poaFront: MultipartBody.Part? = null
                        if (!aadharFrontImg.isEmpty()) {
                            val f1 = File(aadharFrontImg)
                            poaFront =
                                Utils(this).prepareFilePart(
                                    "poaFront",
                                    f1
                                )
                        }

                        var poaBack: MultipartBody.Part? = null
                        if (!aadharBackImg.isEmpty()) {
                            val f1 = File(aadharBackImg)
                            poaBack =
                                Utils(this).prepareFilePart(
                                    "poaBack",
                                    f1
                                )
                        }
                        var licenseFront: MultipartBody.Part? = null
                        if (!drivingFrontImg.isEmpty()) {
                            val f1 = File(drivingFrontImg)
                            licenseFront =
                                Utils(this).prepareFilePart(
                                    "licenseFront",
                                    f1
                                )
                        }

                        var licenseBack: MultipartBody.Part? = null
                        if (!drivingBackImg.isEmpty()) {
                            val f1 = File(drivingBackImg)
                            licenseBack =
                                Utils(this).prepareFilePart(
                                    "licenseBack",
                                    f1
                                )
                        }

                        var panCard: MultipartBody.Part? = null
                        if (!panCardImage.isEmpty()) {
                            val f1 = File(panCardImage)
                            panCard =
                                Utils(this).prepareFilePart(
                                    "panCard",
                                    f1
                                )
                        }

                        if (UtilsFunctions.isNetworkConnected()) {
                            startProgressDialog()
                            docVerifyViewModel.hitDocVerifyApi(
                                mHashMap,
                                poaFront,
                                poaBack,
                                licenseFront,
                                licenseBack,
                                panCard
                            )
                        }
                    }
                    "iv_edit" -> {
                        // editImage = 0
                        //  showPictureDialog()
                    }

                    "cv_aadhar_front" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "cv_adadhr_back" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "cv_pan_card" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "cv_driving_front" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "cv_driving_back" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                }
            })
        )
        docVerifyViewModel.getDocVerify().observe(this,
            Observer<LoginResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            Intent(this, LandingActivty::class.java)
                            startActivity(intent)
                            message?.let { UtilsFunctions.showToastSuccess(it) }
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })

    }

    override fun photoFromCamera(mKey: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
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
                            this,
                            packageName + ".fileprovider",
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
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            profileImage = absolutePath
        }
    }

    override fun photoFromGallery(mKey: String) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun videoFromCamera(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                contentResolver.query(
                    selectedImage!!,
                    filePathColumn,
                    null,
                    null,
                    null
                )
            cursor?.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            profileImage = picturePath
            setAndStoreImage(picturePath)
            //  setImageAadharFront(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            // setImageAadharFront(profileImage)            // val extras = data!!.extras
            setAndStoreImage(profileImage)
            // val imageBitmap = extras!!.get("data") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    private fun setAndStoreImage(picturePath: String) {
        when (buttonClickedId) {
            "cv_aadhar_front" -> {
                setImageAadharFront(picturePath)
                aadharFrontImg = picturePath
            }

            "cv_adadhr_back" -> {
                setImageAadharBack(picturePath)
                aadharBackImg = picturePath
            }

            "cv_pan_card" -> {
                setImagePanCard(picturePath)
                panCardImage = picturePath
            }

            "cv_driving_front" -> {
                setImageDrivingFront(picturePath)
                drivingFrontImg = picturePath
            }

            "cv_driving_back" -> {
                setImageDrivingBack(picturePath)
                drivingBackImg = picturePath
            }

        }
    }

    private fun setImageAadharFront(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_person)
            .into(activityDocVeribinding.frontAdaharIv)
    }

    private fun setImageAadharBack(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_person)
            .into(activityDocVeribinding.backAdaharIv)
    }

    private fun setImagePanCard(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_person)
            .into(activityDocVeribinding.panCardIv)
    }

    private fun setImageDrivingFront(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_person)
            .into(activityDocVeribinding.frontDrivingIv)
    }

    private fun setImageDrivingBack(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_person)
            .into(activityDocVeribinding.backDrivingIv)
    }

    override fun selfieFromCamera(mKey: String) {

    }

}