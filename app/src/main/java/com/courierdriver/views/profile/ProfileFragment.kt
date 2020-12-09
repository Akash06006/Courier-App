package com.courierdriver.views.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import com.courierdriver.model.CommonModel
import com.courierdriver.model.GetVehiclesModel
import com.courierdriver.model.RegionListModel
import com.courierdriver.model.profile.AccountDetailsModel
import com.courierdriver.model.profile.RegionResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.Utils
import com.courierdriver.utils.ValidationsClass
import com.courierdriver.viewmodels.profile.ProfileViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : BaseFragment(), ChoiceCallBack {
    private var reginRes = ArrayList<RegionResponse.Data>()
    private var binding: ActivityProfileBinding? = null
    private lateinit var viewModel: ProfileViewModel
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    private var regionPos = 0
    private var regionId = "0"
    private var selectedRegion: String? = null
    private var vehicleId = "0"
    private var selectedVehicle: String? = null
    private var regionList: ArrayList<RegionListModel.Body>? = ArrayList()
    private var regionStringList: ArrayList<String>? = ArrayList()
    private var vehicleList: ArrayList<GetVehiclesModel.Body>? = ArrayList()
    private var vehicleStringList: ArrayList<String>? = ArrayList()

    override fun initView() {
        binding = viewDataBinding as ActivityProfileBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.profileViewModel = viewModel

        getRegionListObserver()
        getVehicleListObserver()
        updateProfileDetailsObserver()
        viewModel.accountDetails(GlobalConstants.ACCOUNT)
        getProfileDetailsObserver()
        loaderObserver()
        sharedPrefValue()
        makeEnableDisableViews(false)
        viewClicks()
    }

    private fun loaderObserver() {
        viewModel.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    private fun viewClicks() {
        viewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "img_profile" -> {
                        if (baseActivity.checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    activity!!,
                                    this,
                                    "gallery"
                                )
                        }
                    }
                    "iv_edit" -> {
                        makeEnableDisableViews(true)
                    }
                    "btn_submit" -> {
                        val fname = binding!!.etFirstname.text.toString()
                        val lname = binding!!.etLastname.text.toString()
                        val email = binding!!.etEmail.text.toString()
                        val phone = binding!!.etPhone.text.toString()
                        when {
                            fname.isEmpty() -> showError(
                                binding!!.etFirstname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lname.isEmpty() -> showError(
                                binding!!.etLastname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                binding!!.etEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    binding!!.etEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            phone.isEmpty() -> showError(
                                binding!!.etLastname,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            regionId == "0" -> {
                                showToastError(getString(R.string.please_select_region))
                            }
                            else -> {
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
                                    Utils(activity!!).createPartFromString(regionId)
                                mHashMap["vehicleId"] =
                                    Utils(activity!!).createPartFromString(vehicleId)
                                var userImage: MultipartBody.Part? = null
                                if (profileImage.isNotEmpty()) {
                                    val f1 = File(profileImage)
                                    userImage =
                                        Utils(activity!!).prepareFilePart(
                                            "profileImage",
                                            f1
                                        )
                                }
                                    viewModel.updateProfile(mHashMap, userImage)
                            }
                        }
                    }
                }
            })
        )
    }

    private fun updateProfileDetailsObserver() {
        viewModel.updateProfileData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            showToastSuccess(response.message!!)
                            makeEnableDisableViews(false)
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

    private fun getProfileDetailsObserver() {
        viewModel.accountDetailsData().observe(this,
            Observer<AccountDetailsModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            showToastSuccess(response.message!!)

                            binding!!.model = response.body
                            regionId = response.body!!.regionId!!
                            vehicleId = response.body.transportId!!
                            selectedRegion = response.body.regionName!!
                            selectedVehicle = response.body.transportName!!
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

    private fun sharedPrefValue() {
        val userId =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERID).toString()
        val name = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            getString(R.string.fname)
        )
        /*  val userImage =
              SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()
  */
//        setImage(userImage)
    }

    private fun makeEnableDisableViews(isEnable: Boolean) {
        binding!!.etFirstname.isEnabled = isEnable
        binding!!.etLastname.isEnabled = isEnable
        binding!!.etEmail.isEnabled = false
        binding!!.etPhone.isEnabled = false
        binding!!.etPassword.isEnabled = false
        binding!!.etReferenceName.isEnabled = isEnable
        binding!!.etReferenceContact.isEnabled = isEnable
        binding!!.spRegion.isEnabled = isEnable
        binding!!.spTransport.isEnabled = isEnable

        if (!isEnable) {
            binding!!.ivEdit.visibility = View.VISIBLE
            binding!!.btnSubmit.visibility = View.GONE
        } else {
            binding!!.ivEdit.visibility = View.GONE
            binding!!.btnSubmit.visibility = View.VISIBLE
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
            .into(binding!!.imgProfile)
    }

    private fun getRegionListObserver() {
        viewModel.regionList()
        viewModel.regionListData().observe(this,
            Observer<RegionListModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                regionList = response.body
                                if (regionList!!.isNotEmpty())
                                    setRegionsSpinner(binding!!.spRegion)
                            }
                        }
                    }
                } else {
                    showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setRegionsSpinner(spRegions: Spinner) {
        if (regionStringList!!.isNotEmpty())
            regionStringList!!.clear()

        regionList?.let {
            for (item in 0 until regionList!!.size) {
                regionStringList!!.add(regionList!![item].name!!)
            }
        }

        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
        adapter.add(getString(R.string.select_region))
        adapter.addAll(regionStringList!!)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spRegions.adapter = adapter

        spRegions.post {
            regionStringList?.let {
                for (item in 0 until regionStringList!!.size) {
                    if (regionStringList!![item] == selectedRegion) {
                        spRegions.setSelection(item+1)
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
                    regionId = regionList!![position - 1].id!!
                } else {
                    regionId = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun getVehicleListObserver() {
        viewModel.transporterList()
        viewModel.transporterListData().observe(this,
            Observer<GetVehiclesModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                vehicleList = response.body
                                if (vehicleList!!.isNotEmpty())
                                    setVehiclesSpinner(binding!!.spTransport)
                            }
                        }
                    }
                } else {
                    showToastError(resources.getString(R.string.internal_server_error))
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

        spRegions.post {
            vehicleStringList?.let {
                for (item in 0 until vehicleStringList!!.size) {
                    if (vehicleStringList!![item] == selectedVehicle) {
                        spRegions.setSelection(item+1)
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
                    selectedVehicle = vehicleList!![position - 1].name!!
                } else {
                    selectedVehicle = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_profile
    }
}
