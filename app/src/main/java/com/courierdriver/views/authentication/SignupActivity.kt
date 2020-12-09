package com.courierdriver.views.authentication

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.callbacks.ChoiceCallBack
import com.courierdriver.common.FirebaseFunctions
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivitySignupBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.LoginResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.Utils
import com.courierdriver.utils.ValidationsClass
import com.courierdriver.viewmodels.LoginViewModel
import com.courierdriver.views.home.LandingActivty
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var activitySignupbinding: ActivitySignupBinding
    private lateinit var loginViewModel: LoginViewModel
    val mOtpJsonObject = JsonObject()
    var isSocial = false
    var socialType = ""
    var socialId = ""
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()

    override fun getLayoutId(): Int {
        return R.layout.activity_signup
    }

    override fun initViews() {
        activitySignupbinding =
            viewDataBinding as ActivitySignupBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activitySignupbinding.loginViewModel = loginViewModel
        val fbSocial = intent.extras.get("fbSocial").toString()
        val googleSocial = intent.extras.get("googleSocial").toString()
        if (fbSocial.equals("true")) {
            isSocial = true
            socialType = "facebook"
            val fbDetails = JSONObject(intent.extras.get("fbData").toString())
            if (fbDetails.has("id")) {
                socialId = fbDetails.getString("id")
            }
            /*if (fbDetails.has("firstName")) {
                socialId = fbDetails.getString("firstName")
            }
            if (fbDetails.has("lastName")) {
                socialId = fbDetails.getString("lastName")
            }*/
            if (fbDetails.has("name")) {
                val fullname = fbDetails.getString("name")
                if (fullname.contains(" ")) {
                    var name = fullname.split(" ")
                    val firstName = name[0]
                    val lastname = name[1]

                    activitySignupbinding.edtFirstName.setText(firstName)
                    activitySignupbinding.edtLastName.setText(lastname)
                } else {
                    activitySignupbinding.edtFirstName.setText(fullname)
                }
            }
            if (fbDetails.has("email")) {
                val email = fbDetails.getString("email")
                activitySignupbinding.edtEmail.setText(email)

            }
        } else if (googleSocial == "true") {
            isSocial = true
            socialType = "google"
            val fbDetails = JSONObject(intent.extras.get("fbData").toString())
            val email = fbDetails.getString("email")
            socialId = fbDetails.getString("socialId")
            val firstName = fbDetails.getString("firstName")
            val lastName = fbDetails.getString("lastName")
            activitySignupbinding.edtFirstName.setText(firstName)
            activitySignupbinding.edtLastName.setText(lastName)
            activitySignupbinding.edtEmail.setText(email)
        }
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        loginViewModel.getSignupRes().observe(this,
            Observer<LoginResponse> { loginResponse ->
                stopProgressDialog()
                // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        /* SharedPrefClass().putObject(
                             MyApplication.instance,
                             "isLogin",
                             true
                         )*/
                        GlobalConstants.VERIFICATION_TYPE = "signup"
                        FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                        /* mOtpJsonObject.addProperty("phoneNumber", response.data?.phoneNumber)
                         mOtpJsonObject.addProperty("countryCode", response.data?.countryCode)*/
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.ACCESS_TOKEN,
                            loginResponse.data!!.token
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERID,
                            loginResponse.data!!.id
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USER_ID,
                            loginResponse.data!!.id
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.IS_SOCIAL,
                            isSocial
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.REGION_ID,
                            ""
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.IS_DOC_UPLOADED,
                            "false"
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERNAME,
                            activitySignupbinding.edtFirstName.text.toString() + " " + activitySignupbinding.edtLastName.text.toString()
                        )
                        /* val mJsonObject = JsonObject()
                         mJsonObject.addProperty("userId", loginResponse.data!!.id)
                         mJsonObject.addProperty("sessionToken", loginResponse.data!!.token)
                         loginViewModel.callVerifyUserApi(mJsonObject)*/
                        /* showToastSuccess(message)
                         val intent = Intent(this, OTPVerificationActivity::class.java)
                         startActivity(intent)
                         finish()*/

                    } else if (loginResponse.code == 408) {
                        showToastError(message)
                    } else {
                        showToastError(message)
                    }

                }
            })
        /*loginViewModel.getEmailError().observe(this, Observer<String> { emailError->
            activityLoginbinding.etEmail.error = emailError
            activityLoginbinding.etEmail.requestFocus()
        })


        loginViewModel.getPasswordError().observe(this, Observer<String> { passError->
            activityLoginbinding.etPassword.requestFocus()
            activityLoginbinding.etPassword.error = passError
        })*/
        loginViewModel.getVerifyUserRes().observe(this,
            Observer<CommonModel> { loginResponse ->
                stopProgressDialog()
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            "isLogin",
                            true
                        )
                        //showToastSuccess(message)
                        val intent = Intent(this, LandingActivty::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        //showToastError(message)
                    }

                }
            })


        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "img_profile" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        }
                    }
                    "btnSignup" -> {
                        val fName = activitySignupbinding.edtFirstName.text.toString()
                        val lName = activitySignupbinding.edtLastName.text.toString()
                        val email = activitySignupbinding.edtEmail.text.toString()
                        val phone = activitySignupbinding.edtPhone.text.toString()
                        val password = activitySignupbinding.edtPassword.text.toString()
                        val confirmPassword =
                            activitySignupbinding.edtConfirmPassword.text.toString()


                        when {
                            fName.isEmpty() -> showError(
                                activitySignupbinding.edtFirstName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lName.isEmpty() -> showError(
                                activitySignupbinding.edtLastName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                activitySignupbinding.edtEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    activitySignupbinding.edtEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            phone.isEmpty() -> showError(
                                activitySignupbinding.edtPhone,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            phone.length < 10 -> showError(
                                activitySignupbinding.edtPhone,
                                getString(R.string.phone_number) + " " + getString(
                                    R.string.phone_min
                                )
                            )
                            /* password.isEmpty() -> showError(
                                 activitySignupbinding.edtPassword,
                                 getString(R.string.empty) + " " + getString(
                                     R.string.password
                                 )
                             )
                             password.length < 8 -> showError(
                                 activitySignupbinding.edtPassword,
                                 getString(R.string.password_len_msg)
                             )
                             confirmPassword.isEmpty() -> showError(
                                 activitySignupbinding.edtConfirmPassword,
                                 getString(R.string.empty) + " " + getString(
                                     R.string.confirm_password
                                 )
                             )
                             password != confirmPassword -> showError(
                                 activitySignupbinding.edtConfirmPassword,
                                 MyApplication.instance.getString(R.string.mismatch_paaword)
                             )*/
                            else -> {
                                mOtpJsonObject.addProperty(
                                    "countryCode",
                                    "+" + activitySignupbinding.btnCcp.selectedCountryCode
                                )
                                mOtpJsonObject.addProperty("phoneNumber", phone)

                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["countryCode"] =
                                    Utils(this!!).createPartFromString("+" + activitySignupbinding.btnCcp.selectedCountryCode)
                                mHashMap["phoneNumber"] =
                                    Utils(this!!).createPartFromString(phone)
                                mHashMap["firstName"] =
                                    Utils(this!!).createPartFromString(fName)
                                mHashMap["lastName"] =
                                    Utils(this!!).createPartFromString(lName)
                                mHashMap["phoneNumber"] =
                                    Utils(this!!).createPartFromString(phone)
                                mHashMap["email"] =
                                    Utils(this!!).createPartFromString(email)
                                /* mHashMap["password"] =
                                     Utils(this!!).createPartFromString(password)*/
                                mHashMap["isSocial"] =
                                    Utils(this!!).createPartFromString(isSocial.toString())
                                mHashMap["deviceToken"] =
                                    Utils(this!!).createPartFromString("deviceToken")
                                mHashMap["platform"] =
                                    Utils(this!!).createPartFromString("android")
                                mHashMap["socialType"] =
                                    Utils(this!!).createPartFromString("socialType")
                                mHashMap["socialId"] =
                                    Utils(this!!).createPartFromString("socialId")
                                mHashMap["referralCode"] =
                                    Utils(this!!).createPartFromString(activitySignupbinding.etReferralCode.text.toString())

                                var userImage: MultipartBody.Part? = null
                                if (!profileImage.isEmpty()) {
                                    val f1 = File(profileImage)
                                    userImage =
                                        Utils(this!!).prepareFilePart(
                                            "profileImage",
                                            f1
                                        )
                                }

                                if (UtilsFunctions.isNetworkConnected()) {
                                    loginViewModel.callSignupApi(mHashMap, userImage)
                                }
                            }
                        }
                    }
                }
            })
        )
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
            .into(activitySignupbinding.imgProfile)
    }

    private fun showError(textView: EditText, error: String) {
        textView.requestFocus()
        textView.error = error
    }
}