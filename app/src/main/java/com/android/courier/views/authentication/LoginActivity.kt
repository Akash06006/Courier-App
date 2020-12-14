package com.android.courier.views.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityLoginBinding
import com.android.courier.model.LoginResponse
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.ValidationsClass
import com.android.courier.viewmodels.LoginViewModel
import com.android.courier.views.home.LandingActivty
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.JsonObject
import org.json.JSONObject
import com.android.courier.R
import com.android.courier.common.FirebaseFunctions
import java.util.*

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginbinding : ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel
    private var callbackManager : CallbackManager? = null
    private val EMAIL = "email"
    val RC_SIGN_IN : Int = 1
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var mGoogleSignInOptions : GoogleSignInOptions
    private lateinit var firebaseAuth : FirebaseAuth
    val mOtpJsonObject = JsonObject()
    override fun getLayoutId() : Int {
        return R.layout.activity_login
    }

    override fun initViews() {
        if (!checkAndRequestPermissions()) {
            checkAndRequestPermissions()
        }
        val token = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.NOTIFICATION_TOKEN
        )


        activityLoginbinding =
            viewDataBinding as ActivityLoginBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activityLoginbinding.loginViewModel = loginViewModel
        //  Logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        configureGoogleSignIn()
        //setupUI()
        firebaseAuth = FirebaseAuth.getInstance()
        loginViewModel.getLoginRes().observe(this,
            Observer<LoginResponse> { loginResponse->
                stopProgressDialog()

                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
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
                            GlobalConstants.USER_IMAGE,
                            loginResponse.data!!.image
                        )
                        /*SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.IS_SOCIAL,
                            loginResponse.data!!.isSocial
                        )*/
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USEREMAIL,
                            loginResponse.data!!.email
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.PRIVACY_POLICY,
                            loginResponse.data!!.privacyLink
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.ABOUT_US,
                            loginResponse.data!!.aboutUsLink
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.TERMS_CONDITION,
                            loginResponse.data!!.termsLink
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
                            GlobalConstants.REFERRAL_CODE,
                            loginResponse.data!!.referralCode
                        )
                        GlobalConstants.VERIFICATION_TYPE = "login"
                        FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                        //showToastSuccess(message)
                        //val intent = Intent(this, LandingActivty::class.java)
                        //intent.putExtra("catId", ""/*categoriesList[position].id*/)
                        //startActivity(intent)
                        // finish()

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



        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "googleLogin" -> {
                        signIn()
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
                        val phoneNumber = activityLoginbinding.edtEmail.text.toString()
                        //val password = activityLoginbinding.edtPassword.text.toString()
                        when {
                            phoneNumber.isEmpty() -> showError(
                                activityLoginbinding.edtEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.mob_no
                                )
                            )
                            phoneNumber.length < 10 -> showError(
                                activityLoginbinding.edtEmail,
                                getString(R.string.invalid) + " " + getString(
                                    R.string.mob_no
                                )
                            )
                            /*!phoneNumber.matches((ValidationsClass.EMAIL_PATTERN).toRegex())
                        ->
                            showError(
                                activityLoginbinding.edtEmail,
                                getString(R.string.invalid) + " " + getString(
                                    R.string.email
                                )
                            )*/
                            /*  password.isEmpty() -> showError(
                                  activityLoginbinding.edtPassword,
                                  getString(R.string.empty) + " " + getString(
                                      R.string.password
                                  )
                              )*/
                            else -> {
                                mOtpJsonObject.addProperty(
                                    "countryCode",
                                    "+" + 91
                                )
                                mOtpJsonObject.addProperty("phoneNumber", phoneNumber)
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty("phoneNumber", phoneNumber)
                                mJsonObject.addProperty("countryCode", "+91")
                                //mJsonObject.addProperty("password", password)
                                mJsonObject.addProperty("isSocial", false)
                                mJsonObject.addProperty("socialId", "")
                                mJsonObject.addProperty(
                                    "deviceToken",
                                    GlobalConstants.NOTIFICATION_TOKEN
                                )
                                mJsonObject.addProperty("platform", "android")
                                if (UtilsFunctions.isNetworkConnected()) {
                                    loginViewModel.callLoginApi(mJsonObject)
                                }
//                                val intent = Intent(this, LandingActivty::class.java)
//                                startActivity(intent)
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
                                override fun onSuccess(loginResult : LoginResult) {
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
                                        GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response->
                                            try {
                                                //here is the data that you want
                                                Log.d("FBLOGIN_JSON_RES", `object`.toString())

                                                if (`object`.has("id")) {
                                                    handleSignInResultFacebook(`object`)
                                                } else {
                                                    Log.e("FBLOGIN_FAILD", `object`.toString())
                                                }

                                            } catch (e : Exception) {
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

                                override fun onError(error : FacebookException) {
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

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        val signInIntent : Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResultFacebook(jsonObject : JSONObject?) {
        val intent = Intent(this, SignupActivity::class.java)
        intent.putExtra("social", "false")
        intent.putExtra("fbSocial", "true")
        intent.putExtra("googleSocial", "false")
        intent.putExtra("fbData", jsonObject.toString())
        startActivity(intent)

    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e : ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun firebaseAuthWithGoogle(acct : GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // startActivity(HomeActivity.getLaunchIntent(this))
                val jsonObject = JsonObject()
                jsonObject.addProperty("name", acct.displayName)
                jsonObject.addProperty("email", acct.email)
                jsonObject.addProperty("id", acct.id)
                val intent = Intent(this, SignupActivity::class.java)
                intent.putExtra("social", "false")
                intent.putExtra("fbSocial", "false")
                intent.putExtra("googleSocial", "true")
                intent.putExtra("fbData", "")
                intent.putExtra("googleData", jsonObject.toString())
                startActivity(intent)
                // showToastSuccess("Google Login Success")

            } else {
                Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
            }
        }
    }

}