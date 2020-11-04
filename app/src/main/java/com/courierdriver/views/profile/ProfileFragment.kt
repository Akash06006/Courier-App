package com.courierdriver.views.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.callbacks.ChoiceCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.common.UtilsFunctions.showToastError
import com.courierdriver.common.UtilsFunctions.showToastSuccess
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityProfileBinding
import com.courierdriver.model.LoginResponse
import com.courierdriver.model.profile.RegionResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.Utils
import com.courierdriver.utils.ValidationsClass
import com.courierdriver.viewmodels.profile.ProfileViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : BaseFragment(), ChoiceCallBack {
    private var reginRes = ArrayList<RegionResponse.Data>()
    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var profieViewModel: ProfileViewModel
    private var sharedPrefClass: SharedPrefClass? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    private var regionPos = 0
    private var regionId = "0"
    var region = ArrayList<String>()
    override fun getLayoutResId(): Int {
        return R.layout.activity_profile
    }

    override fun initView() {
        profileBinding = viewDataBinding as ActivityProfileBinding
        profieViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        profileBinding.profileViewModel = profieViewModel
        profileBinding.toolbarCommon.imgRight.visibility = View.VISIBLE
        //profileBinding.toolbarCommon.topToolbar.visibility = View.GONE
        profileBinding.toolbarCommon.imgRight.setImageResource(R.drawable.ic_nav_edit_icon)
        profileBinding.toolbarCommon.toolbar.setImageResource(R.drawable.ic_side_menu)
        //  profileBinding.toolbarCommon.imgToolbarText.text =  resources.getString(R.string.view_profile)
        val languages = resources.getStringArray(R.array.Languages)
        /*  profileBinding.btnSubmit.setBackgroundTintList(
              ColorStateList.valueOf(
                  Color.parseColor(
                      GlobalConstants.COLOR_CODE
                  )
              )*//*mContext.getResources().getColorStateList(R.color.colorOrange)*//*
        )*/
        region.add("Select Region")
        val userId =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERID).toString()
        val name = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            getString(R.string.fname)
        )
        /* if (TextUtils.isEmpty(name.toString()) || name.toString().equals("null")) {
             makeEnableDisableViews(true)
         } else {*/
        makeEnableDisableViews(false)
        //}
        mJsonObject.addProperty(
            "userId", userId
        )



        profieViewModel.getDetail().observe(this,
            Observer<LoginResponse> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            profileBinding.model = response.data

                            SharedPrefClass().putObject(
                                activity!!,
                                GlobalConstants.USER_IMAGE,
                                response.data!!.image
                            )
                            SharedPrefClass().putObject(
                                activity!!,
                                getString(R.string.fname),
                                response.data!!.firstName + " " + response.data!!.lastName
                            )
                            if (!TextUtils.isEmpty(response.data?.regionId)) {
                                regionId = response.data?.regionId!!
                                if (region.size > 0) {
                                    for (i in 0 until region.count())
                                        if (regionId.equals(reginRes[i].id)) {
                                            profileBinding.spinner.setSelection(i)
                                        }
                                }
                            }
                            profileBinding.txtEmail.setText(response.data?.email)
                            profileBinding.txtUsername.setText(response.data?.firstName + " " + response.data?.lastName)

                        }
                        else -> message?.let { showToastError(it) }
                    }
                }
            })

        profieViewModel.getRegionsRes().observe(this,
            Observer<RegionResponse> { response ->
                baseActivity.stopProgressDialog()
                if (UtilsFunctions.isNetworkConnected()) {
                    baseActivity.startProgressDialog()
                    profieViewModel.getProfileDetail(mJsonObject)
                }
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            val data = RegionResponse.Data()
                            data.id = "0"
                            data.name = "Select Region"
                            reginRes.add(data)
                            reginRes.addAll(response.data!!)
                            // profileBinding.model = response.data
                            for (count in 0 until response.data!!.count()) {
                                region.add(response.data!![count].name!!)

                            }

                            if (profileBinding.spinner != null) {
                                val adapter = ArrayAdapter(
                                    activity!!,
                                    android.R.layout.simple_spinner_item, region
                                )
                                profileBinding.spinner.adapter = adapter
                                profileBinding.spinner.onItemSelectedListener = object :
                                    AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View, position: Int, id: Long
                                    ) {
                                        if (position != 0) {
                                            regionId = reginRes[position].id!!
                                            regionPos = position
                                        } else {
                                            regionId = "0"
                                            regionPos = position
                                        }

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                        // write code to perform some action
                                    }
                                }
                            }

                        }
                        else -> message?.let { showToastError(it) }
                    }
                }
            })

        profieViewModel.getUpdateDetail().observe(this,
            Observer<LoginResponse> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            // profileBinding.model = response.data
                            message?.let { showToastSuccess(it) }
                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
                                profieViewModel.getProfileDetail(mJsonObject)
                            }
                            /*SharedPrefClass().putObject(
                                activity!!,
                                GlobalConstants.USER_IMAGE,
                                response.data!!.image
                            )
                            SharedPrefClass().putObject(
                                activity!!,
                                getString(R.string.fname),
                                response.data!!.firstName + " " + response.data!!.lastName
                            )
*/
                            makeEnableDisableViews(false)
                        }
                        else -> message?.let { showToastError(it) }
                    }

                }
            })


        profieViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {

                when (it) {
                    "toolbar" -> {
                        showToastError("clicked")
                    }
                    "iv_edit" -> {
                        // editImage = 0
                        //  showPictureDialog()
                    }
                    "img_right" -> {
                        // isEditable = true
                        //  profileBinding.commonToolBar.imgToolbarText.text = resources.getString(R.string.edit_profile)
                        makeEnableDisableViews(true)
                    }
                    "upload_profile_layer" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    activity!!,
                                    this,
                                    "gallery"
                                )
                        }

                    }
                    "btn_submit" -> {
                        val fname = profileBinding.etFirstname.text.toString()
                        val lname = profileBinding.etLastname.text.toString()
                        val email = profileBinding.etEmail.text.toString()
                        val phone = profileBinding.etPhone.text.toString()
                        when {
                            fname.isEmpty() -> showError(
                                profileBinding.etFirstname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lname.isEmpty() -> showError(
                                profileBinding.etLastname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                profileBinding.etEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    profileBinding.etEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            regionId.equals("0") -> {
                                showToastError("Please select region")
                            }
                            else -> {
                                /* val phonenumber = SharedPrefClass().getPrefValue(
                                     MyApplication.instance,
                                     getString(R.string.key_phone)
                                 ) as String
                                 val countrycode = SharedPrefClass().getPrefValue(
                                     MyApplication.instance,
                                     getString(R.string.key_country_code)
                                 ) as String*/
                                val androidId = UtilsFunctions.getAndroidID()
                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["firstName"] =
                                    Utils(activity!!).createPartFromString(fname)
                                mHashMap["lastName"] =
                                    Utils(activity!!).createPartFromString(lname)
                                mHashMap["email"] =
                                    Utils(activity!!).createPartFromString(email)
                                mHashMap["phoneNumber"] =
                                    Utils(activity!!).createPartFromString(phone)
                                mHashMap["regionId"] =
                                    Utils(activity!!).createPartFromString(reginRes[regionPos].id!!)
                                //  mHashMap["password"] = Utils(this).createPartFromString(password)
                                var userImage: MultipartBody.Part? = null
                                if (!profileImage.isEmpty()) {
                                    val f1 = File(profileImage)
                                    userImage =
                                        Utils(activity!!).prepareFilePart(
                                            "profileImage",
                                            f1
                                        )
                                }
                                if (UtilsFunctions.isNetworkConnected()) {
                                    baseActivity.startProgressDialog()
                                    profieViewModel.updateProfile(mHashMap, userImage)
                                }

                            }
                        }

                    }
                }
            })
        )

    }

    private fun makeEnableDisableViews(isEnable: Boolean) {
        profileBinding.etFirstname.isEnabled = isEnable
        profileBinding.etLastname.isEnabled = isEnable
        profileBinding.etEmail.isEnabled = false
        profileBinding.etPhone.isEnabled = false
        profileBinding.spinner.isEnabled = isEnable
        // profileBinding.etAddress.isEnabled = isEnable
        if (!isEnable) {
            profileBinding.ivEdit.visibility = View.GONE
            profileBinding.btnSubmit.visibility = View.GONE
            profileBinding.commonToolBar.imgRight.visibility = View.VISIBLE
        } else {
            profileBinding.ivEdit.visibility = View.VISIBLE
            profileBinding.commonToolBar.imgRight.visibility = View.GONE
            profileBinding.btnSubmit.visibility = View.VISIBLE
        }

    }

    private fun showError(textView: TextView, error: String) {
        textView.requestFocus()
        textView.error = error
    }

    override fun photoFromCamera(mKey: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
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
                            activity!!,
                            activity!!.packageName + ".fileprovider",
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
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                activity!!.contentResolver.query(
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
            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            setImage(profileImage)            // val extras = data!!.extras
            // val imageBitmap = extras!!.get("data") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    private fun setImage(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_person)
            .into(profileBinding.imgProfile)
    }
}
