package com.courierdriver.views.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.courierdriver.R
import com.courierdriver.callbacks.ChoiceCallBack
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityProfileDocumentBinding
import com.courierdriver.model.GetVehiclesModel
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.ProfileDocumentModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.ResizeImage
import com.courierdriver.utils.Utils
import com.courierdriver.viewmodels.DocVerifyViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileDocumentFragment : BaseFragment(), ChoiceCallBack, SelfieCallBack {
    private lateinit var activityDocVeribinding: ActivityProfileDocumentBinding
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
    private var vehicleList: ArrayList<GetVehiclesModel.Body>? = ArrayList()
    private var vehicleStringList: ArrayList<String>? = ArrayList()
    private var vehicleId = "0"

    override fun initView() {
        activityDocVeribinding = viewDataBinding as ActivityProfileDocumentBinding
        docVerifyViewModel = ViewModelProviders.of(this).get(DocVerifyViewModel::class.java)
        activityDocVeribinding.docVerifyViewModel = docVerifyViewModel

        sharedPrefValues()
        makeEnableDisableViews(false)
        clickViews()
        getVehicleListObserver()
        getProfileDetailsObserver()
        verifyDocObserver()
        loaderObserver()
    }

    private fun loaderObserver() {
        docVerifyViewModel.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    private fun sharedPrefValues() {
        userId =
            SharedPrefClass().getPrefValue(baseActivity, GlobalConstants.USER_ID).toString()
    }

    private fun verifyDocObserver() {
        docVerifyViewModel.getDocVerify().observe(this,
            Observer<LoginResponse> { response ->
               baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            makeEnableDisableViews(false)
                            docVerifyViewModel.documentDetails(GlobalConstants.DOCUMENTS_TAB)
                            message?.let { UtilsFunctions.showToastSuccess(it) }
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })
    }

    private fun getProfileDetailsObserver() {
        docVerifyViewModel.documentDetails(GlobalConstants.DOCUMENTS_TAB)
        docVerifyViewModel.documentDetailsData().observe(this,
            Observer<ProfileDocumentModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            // UtilsFunctions.showToastSuccess(response.message!!)
                            activityDocVeribinding.model = response.body
                            makeEnableDisableViews(false)
/*
                            response.body!!.transport?.let {
                                vehicleId = response.body.transport!!.id.toString()
                                transportType = response.body.transport!!.name!!
                            }
*/
/*
                            activityDocVeribinding.spTransport.post {
                                vehicleStringList?.let {
                                    for (item in 0 until vehicleStringList!!.size) {
                                        if (vehicleStringList!![item] == transportType) {
                                            activityDocVeribinding.spTransport.setSelection(item+1)
                                        }
                                    }
                                }
                            }
*/
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

    private fun makeEnableDisableViews(isEnable: Boolean) {
        activityDocVeribinding.llFrontAadhar.isEnabled = isEnable
        activityDocVeribinding.llBackAadhar.isEnabled = isEnable
        activityDocVeribinding.llPanCard.isEnabled = isEnable
        activityDocVeribinding.llFrontDriving.isEnabled = isEnable
        activityDocVeribinding.llBackDriving.isEnabled = isEnable
        if (isEnable) {
            activityDocVeribinding.imgEdit.visibility = View.GONE
            activityDocVeribinding.btnSubmit.visibility = View.VISIBLE
            activityDocVeribinding.llFrontAadhar.visibility = View.VISIBLE
            activityDocVeribinding.llBackAadhar.visibility = View.VISIBLE
            activityDocVeribinding.llPanCard.visibility = View.VISIBLE
            activityDocVeribinding.llFrontDriving.visibility = View.VISIBLE
            activityDocVeribinding.llBackDriving.visibility = View.VISIBLE
        } else {
            activityDocVeribinding.imgEdit.visibility = View.VISIBLE
            activityDocVeribinding.btnSubmit.visibility = View.GONE
            activityDocVeribinding.llFrontAadhar.visibility = View.GONE
            activityDocVeribinding.llBackAadhar.visibility = View.GONE
            activityDocVeribinding.llPanCard.visibility = View.GONE
            activityDocVeribinding.llFrontDriving.visibility = View.GONE
            activityDocVeribinding.llBackDriving.visibility = View.GONE
        }
    }

    private fun clickViews() {
        docVerifyViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                buttonClickedId = it.toString()
 
                when (it) {
                    "img_edit" -> {
                        makeEnableDisableViews(true)
                    }
                    "btn_submit" -> {
                        dlNumber = activityDocVeribinding.etDrivingLicenseNo.text.toString()
                        if(TextUtils.isEmpty(dlNumber))
                        {
                            baseActivity.showToastError("Please enter driving license number")
                        }
                        else {
                            val mHashMap = HashMap<String, RequestBody>()
                            mHashMap["dlNumber"] =
                                Utils(baseActivity).createPartFromString(dlNumber)
                            mHashMap["transportType"] =
                                Utils(baseActivity).createPartFromString(activityDocVeribinding.tvTransport.text.toString())
                            //  mHashMap["password"] = Utils(baseActivity).createPartFromString(password)
                            var poaFront: MultipartBody.Part? = null
                            if (aadharFrontImg.isNotEmpty()) {
                                var f1 = File(aadharFrontImg)
                                f1 = File(ResizeImage.compressImage(aadharFrontImg))
                                poaFront =
                                    Utils(baseActivity).prepareFilePart(
                                        "poaFront",
                                        f1
                                    )
                            }

                            var poaBack: MultipartBody.Part? = null
                            if (aadharBackImg.isNotEmpty()) {
                                var f1 = File(aadharBackImg)
                                f1 = File(ResizeImage.compressImage(aadharBackImg))
                                poaBack =
                                    Utils(baseActivity).prepareFilePart(
                                        "poaBack",
                                        f1
                                    )
                            }
                            var licenseFront: MultipartBody.Part? = null
                            if (drivingFrontImg.isNotEmpty()) {
                                var f1 = File(drivingFrontImg)
                                f1 = File(ResizeImage.compressImage(drivingFrontImg))
                                licenseFront =
                                    Utils(baseActivity).prepareFilePart(
                                        "licenseFront",
                                        f1
                                    )
                            }

                            var licenseBack: MultipartBody.Part? = null
                            if (drivingBackImg.isNotEmpty()) {
                                var f1 = File(drivingBackImg)
                                f1 = File(ResizeImage.compressImage(drivingBackImg))
                                licenseBack =
                                    Utils(baseActivity).prepareFilePart(
                                        "licenseBack",
                                        f1
                                    )
                            }

                            var panCard: MultipartBody.Part? = null
                            if (panCardImage.isNotEmpty()) {
                                var f1 = File(panCardImage)
                                f1 = File(ResizeImage.compressImage(panCardImage))
                                panCard =
                                    Utils(baseActivity).prepareFilePart(
                                        "panCard",
                                        f1
                                    )
                            }

                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
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
                    }
                    "ll_front_aadhar" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    baseActivity,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "ll_back_aadhar" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    baseActivity,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "ll_pan_card" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    baseActivity,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "ll_front_driving" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    baseActivity,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "ll_back_driving" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    baseActivity,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                }
            })
        )
    }

    override fun photoFromCamera(mKey: String) {
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
                baseActivity.contentResolver.query(
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
            "ll_front_aadhar" -> {
                Log.d("TAG","cv_aadhar_front")
                setImageAadharFront(picturePath)
                aadharFrontImg = picturePath
            }
            "ll_back_aadhar" -> {
                setImageAadharBack(picturePath)
                aadharBackImg = picturePath
            }
            "ll_pan_card" -> {
                setImagePanCard(picturePath)
                panCardImage = picturePath
            }
            "ll_front_driving" -> {
                setImageDrivingFront(picturePath)
                drivingFrontImg = picturePath
            }
            "ll_back_driving" -> {
                setImageDrivingBack(picturePath)
                drivingBackImg = picturePath
            }

        }
    }

    private fun setImageAadharFront(path: String) {
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading_image)
            .into(activityDocVeribinding.frontAdaharIv)
    }

    private fun setImageAadharBack(path: String) {
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading_image)
            .into(activityDocVeribinding.backAdaharIv)
    }

    private fun setImagePanCard(path: String) {
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading_image)
            .into(activityDocVeribinding.panCardIv)
    }

    private fun setImageDrivingFront(path: String) {
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading_image)
            .into(activityDocVeribinding.frontDrivingIv)
    }

    private fun setImageDrivingBack(path: String) {
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading_image)
            .into(activityDocVeribinding.backDrivingIv)
    }

    override fun selfieFromCamera(mKey: String) {

    }

    private fun getVehicleListObserver() {
        docVerifyViewModel.transporterList()
        docVerifyViewModel.transporterListData().observe(this,
            Observer<GetVehiclesModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                vehicleList = response.body
                                if (vehicleList!!.isNotEmpty()) {
                                    vehicleId = vehicleList!![0].id.toString()
                                    activityDocVeribinding.tvTransport.text = vehicleList!![0].name
                                }

                                  //  setVehiclesSpinner(activityDocVeribinding.spTransport)
                            }
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setVehiclesSpinner(spRegions: Spinner) {
        if (vehicleStringList!!.isNotEmpty())
            vehicleStringList!!.clear()

        vehicleList?.let {
            for (item in 0 until vehicleList!!.size) {
                vehicleStringList!!.add(vehicleList!![item].name!!)
            }
        }

        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
        adapter.add(getString(R.string.select_vehicle))
        adapter.addAll(vehicleStringList!!)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spRegions.adapter = adapter

        spRegions.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    vehicleId = vehicleList!![position - 1].id!!
                    transportType = vehicleList!![position - 1].name!!
                } else {
                    transportType = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_profile_document
    }
}