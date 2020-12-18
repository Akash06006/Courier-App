package com.courierdriver.views.authentication

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.callbacks.ChoiceCallBack
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityDocumentVerificatonBinding
import com.courierdriver.model.GetVehiclesModel
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
    private var vehicleList: ArrayList<GetVehiclesModel.Body>? = ArrayList()
    private var vehicleStringList: ArrayList<String>? = ArrayList()
    private var vehicleId = "0"
    var userId = ""
    private var isPaytmVisible = false
    private var isPhonePeVisible = false
    private var isGooglePayVisible = false
    private var count = 0
    private var paymentType = ""
    private var paytm = ""
    private var googlePay = ""
    private var phonePe = ""
    private var notificationToken = ""

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

        loaderObserver()
        sharedPrefValue()
        getVehicleListObserver()

        docVerifyViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                buttonClickedId = it.toString()
                when (it) {

                    "rel_phone_pe" -> {
                        if (isPhonePeVisible) {
                            isPhonePeVisible = false
                            activityDocVeribinding.linPhonePe.visibility = View.GONE
                            activityDocVeribinding.imgArrowPhonePe.setImageDrawable(
                                getDrawable(
                                    R.drawable.ic_small_arrow_down
                                )
                            )
                        } else {
                            isPhonePeVisible = true
                            activityDocVeribinding.linPhonePe.visibility = View.VISIBLE
                            activityDocVeribinding.imgArrowPhonePe.setImageDrawable(
                                getDrawable(
                                    R.drawable.ic_up_arrow
                                )
                            )
                        }
                    }
                    "rel_google_pay" -> {
                        if (isGooglePayVisible) {
                            isGooglePayVisible = false
                            activityDocVeribinding.linGooglePay.visibility = View.GONE
                            activityDocVeribinding.imgArrowGooglePay.setImageDrawable(
                                getDrawable(
                                    R.drawable.ic_small_arrow_down
                                )
                            )
                        } else {
                            isGooglePayVisible = true
                            activityDocVeribinding.linGooglePay.visibility = View.VISIBLE
                            activityDocVeribinding.imgArrowGooglePay.setImageDrawable(
                                getDrawable(
                                    R.drawable.ic_up_arrow
                                )
                            )
                        }
                    }
                    "rel_paytm" -> {
                        if (isPaytmVisible) {
                            isPaytmVisible = false
                            activityDocVeribinding.linPaytm.visibility = View.GONE
                            activityDocVeribinding.imgArrowPaytm.setImageDrawable(
                                getDrawable(
                                    R.drawable.ic_small_arrow_down
                                )
                            )
                        } else {
                            isPaytmVisible = true
                            activityDocVeribinding.linPaytm.visibility = View.VISIBLE
                            activityDocVeribinding.imgArrowPaytm.setImageDrawable(
                                getDrawable(
                                    R.drawable.ic_up_arrow
                                )
                            )
                        }
                    }
                    "btn_submit" -> {
                        dlNumber = activityDocVeribinding.etDrivingLicenseNo.text.toString()
                        count = 0
                        paymentType = ""
                        paytm = ""
                        googlePay = ""
                        phonePe = ""

                        if (!TextUtils.isEmpty(activityDocVeribinding.etPhonePaytm.text.toString())) {
                            count += 1
                            paytm = "Paytm" + ","
                        }
                        if (!TextUtils.isEmpty(activityDocVeribinding.etPhoneGooglePay.text.toString())) {
                            count += 1
                            googlePay = "Google Pay" + ","
                        }
                        if (!TextUtils.isEmpty(activityDocVeribinding.etPhonePhonePe.text.toString())) {
                            count += 1
                            phonePe = "Phone Pay"
                        }

                        //  mHashMap["password"] = Utils(this).createPartFromString(password)
                        if (TextUtils.isEmpty(aadharFrontImg)) {
                            showToastError("Please select Aadhar front photo")
                        } else if (TextUtils.isEmpty(aadharBackImg)) {
                            showToastError("Please select Aadhar back photo")
                        } else if (TextUtils.isEmpty(panCardImage)) {
                            showToastError("Please select PAN card image")
                        } else if (TextUtils.isEmpty(transportType)) {
                            showToastError("Please select vehicle type")
                        } else if (TextUtils.isEmpty(dlNumber)) {
                            showToastError("Please enter driving license number")
                        } else if (TextUtils.isEmpty(drivingFrontImg)) {
                            showToastError("Please select Driving license front image")
                        } else if (TextUtils.isEmpty(drivingBackImg)) {
                            showToastError("Please select Driving license back image")
                        } else if (!TextUtils.isEmpty(activityDocVeribinding.etPhonePaytm.text.toString())
                            && (activityDocVeribinding.etPhonePaytm.text.toString()).length < 10
                        ) {
                            showError(
                                activityDocVeribinding.etPhonePaytm,
                                getString(R.string.paytm) + " " + getString(R.string.phone_number)
                                        + " " + getString(R.string.phone_min)
                            )
                        } else if (!TextUtils.isEmpty(activityDocVeribinding.etPhoneGooglePay.text.toString())
                            && (activityDocVeribinding.etPhoneGooglePay.text.toString()).length < 10
                        ) {
                            showError(
                                activityDocVeribinding.etPhoneGooglePay,
                                getString(R.string.google_pay) + " " + getString(R.string.phone_number)
                                        + " " + getString(R.string.phone_min)
                            )
                        } else if (!TextUtils.isEmpty(activityDocVeribinding.etPhonePhonePe.text.toString())
                            && (activityDocVeribinding.etPhonePhonePe.text.toString()).length < 10
                        ) {
                            showError(
                                activityDocVeribinding.etPhonePhonePe,
                                getString(R.string.phone_pe) + " " + getString(R.string.phone_number)
                                        + " " + getString(R.string.phone_min)
                            )
                        } else if (count < 2) {
                            UtilsFunctions.showToastError(getString(R.string.payment_options_error))
                        } else {
                            paymentType = paytm + googlePay + phonePe

                            val mHashMap = HashMap<String, RequestBody>()
                            mHashMap["dlNumber"] =
                                Utils(this).createPartFromString(dlNumber)
                            mHashMap["transportType"] =
                                Utils(this).createPartFromString(transportType)
                            mHashMap["paymentType"] =
                                Utils(this).createPartFromString(paymentType)
                            mHashMap["gpayNo"] =
                                Utils(this).createPartFromString(activityDocVeribinding.etPhoneGooglePay.text.toString())
                            mHashMap["phonePayNo"] =
                                Utils(this).createPartFromString(activityDocVeribinding.etPhonePhonePe.text.toString())
                            mHashMap["paytmNo"] =
                                Utils(this).createPartFromString(activityDocVeribinding.etPhonePaytm.text.toString())

                            var poaFront: MultipartBody.Part? = null
                            if (aadharFrontImg.isNotEmpty()) {
                                val f1 = File(aadharFrontImg)
                                poaFront =
                                    Utils(this).prepareFilePart(
                                        "poaFront",
                                        f1
                                    )
                            }

                            var poaBack: MultipartBody.Part? = null
                            if (aadharBackImg.isNotEmpty()) {
                                val f1 = File(aadharBackImg)
                                poaBack =
                                    Utils(this).prepareFilePart(
                                        "poaBack",
                                        f1
                                    )
                            }
                            var licenseFront: MultipartBody.Part? = null
                            if (drivingFrontImg.isNotEmpty()) {
                                val f1 = File(drivingFrontImg)
                                licenseFront =
                                    Utils(this).prepareFilePart(
                                        "licenseFront",
                                        f1
                                    )
                            }

                            var licenseBack: MultipartBody.Part? = null
                            if (drivingBackImg.isNotEmpty()) {
                                val f1 = File(drivingBackImg)
                                licenseBack =
                                    Utils(this).prepareFilePart(
                                        "licenseBack",
                                        f1
                                    )
                            }

                            var panCard: MultipartBody.Part? = null
                            if (panCardImage.isNotEmpty()) {
                                val f1 = File(panCardImage)
                                panCard =
                                    Utils(this).prepareFilePart(
                                        "panCard",
                                        f1
                                    )
                            }


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
                    when (response.code) {
                        200 -> {
                            SharedPrefClass().putObject(
                                MyApplication.instance,
                                GlobalConstants.IS_DOC_UPLOADED,
                                "true"
                            )
                            val intent1 = Intent(this, LandingActivty::class.java)
                            startActivity(intent1)
                            finish()
                            message?.let { UtilsFunctions.showToastSuccess(it) }
                        }
                        408 -> {
                            showToastError("User not available anymore")
                            SharedPrefClass().putObject(
                                this,
                                "isLogin",
                                false
                            )
                            SharedPrefClass().clearAll(this)
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.NOTIFICATION_TOKENPref,
                                notificationToken
                            )
                            GlobalConstants.NOTIFICATION_TOKEN = notificationToken
                            val i = Intent(
                                MyApplication.instance.applicationContext,
                                LoginActivity::class.java
                            )
                            i.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })

    }

    private fun sharedPrefValue() {
        notificationToken =
            SharedPrefClass().getPrefValue(this, GlobalConstants.NOTIFICATION_TOKENPref)
                .toString()
    }

    private fun showError(textView: EditText, error: String) {
        textView.requestFocus()
        textView.error = error
    }

    private fun getVehicleListObserver() {
        docVerifyViewModel.transporterList()
        docVerifyViewModel.transporterListData().observe(this,
            Observer<GetVehiclesModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                vehicleList = response.body
                                if (vehicleList!!.isNotEmpty())
                                    setVehiclesSpinner(activityDocVeribinding.spTransport)
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

        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item)
        adapter.add(getString(R.string.select_vehicle))
        adapter.addAll(vehicleStringList!!)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spRegions.adapter = adapter

        spRegions.post {
            vehicleStringList?.let {
                for (item in 0 until vehicleStringList!!.size) {
                    if (vehicleStringList!![item] == transportType) {
                        spRegions.setSelection(item + 1)
                    }
                }
            }
        }

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

    private fun loaderObserver() {
        docVerifyViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }
}