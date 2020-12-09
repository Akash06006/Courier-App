package com.courierdriver.views.authentication

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.common.FirebaseFunctions
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityLoginBinding
import com.courierdriver.model.LoginResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.*

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginbinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private var callbackManager: CallbackManager? = null
    private val EMAIL = "email"
    val mOtpJsonObject = JsonObject()
    private val RC_SIGN_IN: Int = 0
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initViews() {
        activityLoginbinding =
            viewDataBinding as ActivityLoginBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activityLoginbinding.loginViewModel = loginViewModel
        //  Logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("6946227711-va1qdqpru77knadr33pnseqnn9l89hgf.apps.googleusercontent.com")
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        loginViewModel.getLoginRes().observe(this,
            Observer<LoginResponse> { loginResponse ->
                stopProgressDialog()

                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        GlobalConstants.VERIFICATION_TYPE = "login"
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            "isLogin",
                            false
                        )
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
                            GlobalConstants.USER_IMAGE,
                            loginResponse.data!!.image
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.IS_DOC_UPLOADED,
                            loginResponse.data!!.isDocUploaded
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.IS_SOCIAL,
                            loginResponse.data!!.isSocial
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERNAME,
                            loginResponse.data!!.firstName + " " + loginResponse.data!!.lastName
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.CUSTOMER_IAMGE,
                            loginResponse.data!!.image
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.REGION_ID,
                            loginResponse.data!!.regionId
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.PHONE_NUMBER,
                            loginResponse.data!!.phoneNo
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.EMAIL,
                            loginResponse.data!!.email
                        )

                        mOtpJsonObject.addProperty(
                            "countryCode",
                            "+" + activityLoginbinding.btCountryCode.selectedCountryCode
                        )
                        mOtpJsonObject.addProperty(
                            "phoneNumber",
                            activityLoginbinding.edtPhone.text.toString()
                        )

                        showToastSuccess(message)
                        GlobalConstants.VERIFICATION_TYPE = "signup"
                        FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)

                        /*val intent = Intent(this, LandingActivty::class.java)
                        startActivity(intent)
                        finish()*/

                    } else {
                        showToastError(message)
                    }

                }
            })

        loaderObserver()
        viewClicks()
    }

    private fun viewClicks() {
        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "google_login" -> {
                        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
                        startActivityForResult(signInIntent, RC_SIGN_IN)
                    }
                    "txtSignup" -> {
                        val intent = Intent(this, SignupActivity::class.java)
                        intent.putExtra("social", "false"/*categoriesList[position].id*/)
                        intent.putExtra("fbSocial", "false"/*categoriesList[position].id*/)
                        intent.putExtra("googleSocial", "false"/*categoriesList[position].id*/)
                        startActivity(intent)
                    }
                    "txtForgot" -> {
                        val intent = Intent(this, ForgotPasswrodActivity::class.java)
                        //intent.putExtra("catId", ""/*categoriesList[position].id*/)
                        startActivity(intent)
                    }
                    "btnLogin" -> {
                        val email = activityLoginbinding.edtEmail.text.toString()
                        val password = activityLoginbinding.edtPassword.text.toString()
                        val phoneNumber = activityLoginbinding.edtPhone.text.toString()

                        when {
                            /*email.isEmpty() -> showError(
                                activityLoginbinding.edtEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    activityLoginbinding.edtEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            password.isEmpty() -> showError(
                                activityLoginbinding.edtPassword,
                                getString(R.string.empty) + " " + getString(
                                    R.string.password
                                )
                            )*/
                            phoneNumber.isEmpty() -> showError(
                                activityLoginbinding.edtPhone,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            phoneNumber.length < 10 -> showError(
                                activityLoginbinding.edtPhone,
                                getString(R.string.phone_number) + " " + getString(
                                    R.string.phone_min
                                )
                            )
                            else -> {
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty("phoneNumber", phoneNumber)
                                mJsonObject.addProperty(
                                    "countryCode",
                                    "+" +  activityLoginbinding.btCountryCode.selectedCountryCode
                                )
                                val android_id: String = Settings.Secure.getString(
                                    applicationContext.contentResolver,
                                    Settings.Secure.ANDROID_ID)
                                /*mJsonObject.addProperty("email", email)
                                mJsonObject.addProperty("password", password)*/
                                mJsonObject.addProperty("isSocial", false)
                                mJsonObject.addProperty("socialId", "")
                                mJsonObject.addProperty("deviceToken", android_id)
                                mJsonObject.addProperty("platform", "android")
                                if (UtilsFunctions.isNetworkConnected()) {
                                    loginViewModel.callLoginApi(mJsonObject)
                                }
                            }
                        }
                    }
                    "fbLogin" -> {
                        callbackManager = CallbackManager.Factory.create()
                        LoginManager.getInstance().logInWithReadPermissions(
                            this,
                            Arrays.asList(
                                "public_profile",
                                "email",
                                "user_birthday",
                                "user_friends"
                            )
                        )
                        LoginManager.getInstance().registerCallback(callbackManager,
                            object : FacebookCallback<LoginResult> {
                                override fun onSuccess(loginResult: LoginResult) {
                                    /*val profile : Profile = Profile.getCurrentProfile()

                                    val fName = profile.firstName
                                    val lName = profile.lastName
                                    val fbId = profile.id
                                    var email = ""


                                    Log.e(
                                        "LoginActivity",
                                        "Facebook token: " + loginResult.accessToken.token
                                    )
                                    Log.e(
                                        "LoginActivity",
                                        "Facebook detail: " + loginResult.toString()
                                    )
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            SignupActivity::class.java
                                        )
                                    )*/
                                    val request =
                                        GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                                            try {
                                                //here is the data that you want
                                                Log.d("FBLOGIN_JSON_RES", `object`.toString())

                                                if (`object`.has("id")) {
                                                    handleSignInResultFacebook(`object`)
                                                } else {
                                                    Log.e("FBLOGIN_FAILD", `object`.toString())
                                                }

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                // dismissDialogLogin()
                                            }
                                        }
                                    val parameters = Bundle()
                                    parameters.putString(
                                        "fields",
                                        "name,email,id,picture.type(large)"
                                    )
                                    request.parameters = parameters
                                    request.executeAsync()
                                }

                                override fun onCancel() {
                                    Log.d("LoginActivity", "Facebook onCancel.")

                                }

                                override fun onError(error: FacebookException) {
                                    Log.d("LoginActivity", "Facebook onError.")

                                }
                            })
                        /* activityLoginbinding.fbLogin.setReadPermissions(asList(EMAIL))
                         // If you are using in a fragment, call loginButton.setFragment(this);
                         // Callback registration
                         // If you are using in a fragment, call loginButton.setFragment(this);
 // Callback registration
                         activityLoginbinding.fbLogin.registerCallback(
                             callbackManager,
                             object : FacebookCallback<LoginResult?> {
                                 override fun onSuccess(loginResult : LoginResult?) { // App code
                                 }

                                 override fun onCancel() { // App code
                                 }

                                 override fun onError(exception : FacebookException) { // App code
                                 }
                             })*/
                    }
                }
            })
        )
    }

    private fun loaderObserver() {
        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    private fun handleSignInResultFacebook(jsonObject: JSONObject?) {
        val intent = Intent(this, SignupActivity::class.java)
        intent.putExtra("social", "false")
        intent.putExtra("fbSocial", "true")
        intent.putExtra("googleSocial", "false")
        intent.putExtra("fbData", jsonObject.toString())
        startActivity(intent)

    }

    private fun showError(textView: EditText, error: String) {
        textView.requestFocus()
        textView.error = error
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
        }
        else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult<ApiException>(ApiException::class.java)
            if (account == null) {
                 Toast.makeText(this, "Somthing went wrong", Toast.LENGTH_LONG).show()

                return
            }
            val input = JsonObject()
            input.addProperty("email",account!!.email)
            input.addProperty("socialId",account!!.id)
            input.addProperty("firstName",account!!.givenName)
            input.addProperty("lastName",account!!.familyName)

            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("social", "false")
            intent.putExtra("fbSocial", "false")
            intent.putExtra("googleSocial", "true")
            intent.putExtra("fbData", input.toString())
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //Toast.makeText(this, "Somthing went wrong", Toast.LENGTH_LONG).show()
        }
    }

}