package com.courierdriver.views.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.courierdriver.R
import com.courierdriver.callbacks.ChoiceCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivitySpotAndErrorBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.GetSubjectsModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.Utils
import com.courierdriver.viewmodels.SpotAndErrorViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class SpotAndErrorActivity : BaseActivity(), ChoiceCallBack {
    private var binding: ActivitySpotAndErrorBinding? = null
    private var viewModel: SpotAndErrorViewModel? = null
    private var subjectList: ArrayList<GetSubjectsModel.Body>? = ArrayList()
    private var subjectStringList: ArrayList<String>? = ArrayList()
    private var subject = ""
    private val GALLERY = 1
    private val CAMERA = 2
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private var imagePath = ""
    private var utils = Utils(this)
    var imageMultipart: MultipartBody.Part? = null
    private var email: String? = null
    private var phoneNumber: String? = null

    override fun initViews() {
        binding = viewDataBinding as ActivitySpotAndErrorBinding
        viewModel = ViewModelProviders.of(this).get(SpotAndErrorViewModel::class.java)
        binding!!.viewModel = viewModel

        sharedPrefValue()
        setToolbarTextIcons()
        getSubjectListObserver()
        addComplaintObserver()
        loaderObserver()
        viewClicks()
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


    private fun sharedPrefValue() {
        email = SharedPrefClass().getPrefValue(this, GlobalConstants.EMAIL)
            .toString()
        phoneNumber = SharedPrefClass().getPrefValue(this, GlobalConstants.PHONE_NUMBER)
            .toString()
    }

    private fun setToolbarTextIcons() {
        binding!!.toolbarCommon.toolbar.setImageResource(R.drawable.ic_back)
        binding!!.toolbarCommon.imgRight.visibility = View.GONE
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.report_for_problem)
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "card_view_image" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }
                    }
                    "tv_submit" -> {
                        when {
                            TextUtils.isEmpty(subject) -> {
                                showToastError(getString(R.string.please_select_subject))
                            }
                            TextUtils.isEmpty(binding!!.etDesc.text.toString()) -> {
                                showToastError(getString(R.string.please_enter_description))
                            }
                            else -> {
                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["email"] = utils.createPartFromString(email!!)
                                mHashMap["query"] =
                                    utils.createPartFromString(binding!!.etDesc.text.toString())
                                mHashMap["subject"] = utils.createPartFromString(subject)
                                mHashMap["phoneNumber"] = utils.createPartFromString(phoneNumber!!)
                                if (imagePath != "") {
                                    val f1 = File(imagePath)
                                    imageMultipart = utils.prepareFilePart("image", f1)
                                }
                                viewModel!!.addComplaints(imageMultipart, mHashMap)
                            }
                        }
                    }
                }
            })
        )
    }

    private fun addComplaintObserver() {
        viewModel!!.addComplaintListData().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            showToastSuccess(response.message)
                            finish()
                        }
                        else -> {
                            showToastError(response.message)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }


    private fun getSubjectListObserver() {
        viewModel!!.subjectList()
        viewModel!!.subjectListData().observe(
            this,
            Observer<GetSubjectsModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                subjectList = response.body
                                if (subjectList!!.isNotEmpty())
                                    setSubjectListSpinner()
                            }
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setSubjectListSpinner() {
        if (subjectStringList!!.isNotEmpty())
            subjectStringList!!.clear()

        for (item in 0 until subjectList!!.size) {
            subjectStringList!!.add(subjectList!![item].subject!!)
        }
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item)
        adapter.add(getString(R.string.select_subject))
        adapter.addAll(subjectStringList!!)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding!!.spSubjects.adapter = adapter

        binding!!.spSubjects.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    subject = subjectList!![position - 1].subject!!
                } else {
                    subject = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    override fun photoFromCamera(mKey: String) {
        takePhotoFromCamera()
    }

    override fun photoFromGallery(mKey: String) {
        choosePhotoFromGallary()
    }


    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    saveImage(bitmap)
                    setImageView(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            setImageView(thumbnail)
        }
    }

    private fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    private fun setImageView(bitmap: Bitmap) {
        imagePath = saveImage(bitmap)
        Glide.with(this).load(imagePath).placeholder(R.drawable.user).into(binding!!.imgView)
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/driver"
    }

    override fun videoFromCamera(mKey: String) {
    }

    override fun videoFromGallery(mKey: String) {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_spot_and_error
    }
}