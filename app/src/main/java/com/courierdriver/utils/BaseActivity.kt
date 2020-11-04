package com.courierdriver.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.views.authentication.LoginActivity
import com.google.gson.GsonBuilder
import java.util.*

/**
 * Created by Saira on 2018-12-9.
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var viewDataBinding: ViewDataBinding? = null
    private var utils: Utils? = null
    private var inputMethodManager: InputMethodManager? = null
    private var progressDialog: Dialog? = null
    private var mFragmentManager: androidx.fragment.app.FragmentManager? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var permCallback: PermissionCallback? = null
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this@BaseActivity.overridePendingTransition(
            R.anim.slide_in,
            R.anim.slide_out
        )

        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        //  mtoolbar = findViewById(R.id.toolbar);
        //        retrofitClient = (ApiService) RetrofitClient.with(this).getClient(Constants.API_BASE_URL, false, this).create(ApiService.class);
        inputMethodManager = this
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //store = new PrefStore(this);
        //    public ApiService retrofitClient;
        strictModeThread()
        initializeProgressDialog()
        initViews()

    }

    protected abstract fun initViews()
    protected abstract fun getLayoutId(): Int

    private fun strictModeThread() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        )
    }

    fun eventCreatedDialog(activity: Activity, key: String, message: String) {
        showToastSuccess(message)
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.activity_dialog_tick)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgTick = dialog.findViewById(R.id.img_tick) as ImageView
        object : CountDownTimer(500, 200) {
            override fun onTick(millisUntilFinished: Long) {
                (imgTick.drawable as Animatable).start()
            }

            override fun onFinish() {
                when (key) {
                    "reset_password" -> {
                        val intent1 = Intent(activity, LoginActivity::class.java)
                        activity.startActivity(intent1)
                        ActivityCompat.finishAffinity(activity)
                    }
                    "cancel_activity" -> {
                        activity.finish()
                    }
                    "change_password" -> {
                        activity.finish()
                    }
                    "cancel" -> dialog.dismiss()
                    "update" -> dialog.dismiss()
                    "update_profile" -> dialog.dismiss()
                }
            }

        }.start()
        dialog.show()

    }

    fun checkObjectNull(obj: Any?): Boolean {
        return obj != null
    }

    fun showAlert(activity: Activity, message1: String) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.layout_custom_alert,
                null,
                false
            )
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(getString(R.string.app_name))
        // set the custom dialog components - text, image and button
        val text = dialog.findViewById(R.id.text) as TextView
        text.text = message1
        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    /*}
     *Method called  progress dialog */
    private fun initializeProgressDialog() {
        progressDialog = Dialog(this, R.style.transparent_dialog_borderless)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this),
            R.layout.progress_dialog,
            null,
            false
        )
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog!!.setContentView(binding.root)

        mFragmentManager = supportFragmentManager
        // txtMsgTV = (TextView) view.findViewById(R.id.txtMsgTV);
        progressDialog!!.setCancelable(false)
    }

    fun showToastSuccess(message: String?) {
        UtilsFunctions.showToastSuccess(message!!)

    }

    fun showToastError(message: String?) {
        UtilsFunctions.showToastError(message!!)

    }

    private fun showToastWarning(message: String?) {
        UtilsFunctions.showToastWarning(message)

    }

    /*
     * Method to show snack bar*/
    /*
     * Method to start progress dialog*/
    fun startProgressDialog() {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            try {
                progressDialog!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /*
     * Method to stop progress dialog*/
    fun stopProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun utils(): Utils {
        if (utils == null)
            utils = Utils(this)
        return utils!!
    }

    fun callFragments(
        fragment: androidx.fragment.app.Fragment?,
        mFragmentManager: androidx.fragment.app.FragmentManager,
        mSendDataCheck: Boolean,
        key: String?,
        mObject: Any
    ) {
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        if (fragment != null) {
            if (mSendDataCheck && key != null) {
                when (key) {
                    "deleteAll" -> mFragmentManager.popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    "send_data" -> {
                        val mBundle = Bundle()
                        mBundle.putString(GlobalConstants.SEND_DATA, gson.toJson(mObject))
                        fragment.arguments = mBundle
                    }
                }
            }
            mFragmentTransaction.addToBackStack(null)
            mFragmentTransaction.replace(R.id.frame_layout, fragment)
            //  mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            mFragmentTransaction.commit()
        }
    }

    fun callFragmentsContainer(
        fragment: androidx.fragment.app.Fragment?,
        mFragmentManager: androidx.fragment.app.FragmentManager,
        mSendDataCheck: Boolean,
        key: String?,
        mObject: Any,
        id: Int
    ) {
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        if (fragment != null) {
            if (mSendDataCheck && key != null) {
                when (key) {
                    "deleteAll" -> mFragmentManager.popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    "send_data" -> {
                        val mBundle = Bundle()
                        mBundle.putString(GlobalConstants.SEND_DATA, gson.toJson(mObject))
                        fragment.arguments = mBundle
                    }
                }
            }
            mFragmentTransaction.addToBackStack(null)
            mFragmentTransaction.replace(id, fragment)
            // mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            mFragmentTransaction.commit()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionUtil.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this,
            permCallback
        )
    }

    interface PermissionCallback {
        fun permGranted()

        fun permDenied()
    }

    public fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionRecordAudio =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val listPermissionsNeeded = ArrayList<String>()

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun newCallFragments(
        fragment: androidx.fragment.app.Fragment?,
        mFragmentManager: androidx.fragment.app.FragmentManager
    ) {
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        if (fragment != null) {
//            mFragmentTransaction.addToBackStack(null)
            mFragmentTransaction.replace(R.id.frame_layout, fragment)
//             mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            mFragmentTransaction.commit()
        }
    }
}
