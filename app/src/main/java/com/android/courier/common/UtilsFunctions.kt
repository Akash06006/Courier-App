package com.android.courier.common

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.courier.R
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.android.courier.application.MyApplication
import java.text.SimpleDateFormat
import java.util.*

object UtilsFunctions {
    @JvmStatic
    fun showToastError(message : String) {
        val toast = Toast.makeText(MyApplication.instance, message, LENGTH_LONG) as Toast
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        val view = toast.view
        val group = toast.view as ViewGroup
        val messageTextView = group.getChildAt(0) as TextView
        messageTextView.textSize = 16.0f
        messageTextView.setPadding(10, 10, 10, 10)
        toast.setMargin(0f, 0f)
        messageTextView.gravity = Gravity.CENTER
        view.setBackgroundColor(ContextCompat.getColor(MyApplication.instance, R.color.colorRed))
        toast.show()
    }

    @JvmStatic
    fun showToastSuccess(message : String) {
        val inflater =
            MyApplication.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.layout_toast, null)
        val image = layout.findViewById<ImageView>(R.id.image)
        image.setImageResource(R.drawable.ic_check)
        val text = layout.findViewById<TextView>(R.id.text)
        text.text = message
        val toast = Toast(MyApplication.instance)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = LENGTH_LONG
        layout.setBackgroundColor(
            ContextCompat.getColor(
                MyApplication.instance,
                R.color.colorSuccess
            )
        )
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        toast.view = layout
        toast.show()

    }

    @SuppressLint("SimpleDateFormat")
    fun getParticularDay(amount : Int) : String {
        val dateFormat = SimpleDateFormat("MMM dd")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, amount)
        val newDate = calendar.time
        return dateFormat.format(newDate)
    }

    @JvmStatic
    fun checkObjectNull(obj : Any?) : Boolean {
        return obj != null
    }

    @SuppressLint("HardwareIds")
    @JvmStatic
    fun getAndroidID() : String {
        return Settings.Secure.getString(
            MyApplication.instance.contentResolver,
            Settings.Secure.ANDROID_ID
        )

    }

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.M)
    fun isNetworkConnected() : Boolean {
        val cm = MyApplication.instance.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork : NetworkInfo? = null
        activeNetwork = cm.activeNetworkInfo

        return if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
            activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } else {
            showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
            // Toast.makeText(MyApplication.getInstance().getApplicationContext(), R.string.internet_connection, Toast.LENGTH_SHORT).show();
            false
        }
    }

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.M)
    fun isNetworkConnectedReturn() : Boolean {
        val cm = MyApplication.instance.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork : NetworkInfo? = null
        activeNetwork = cm.activeNetworkInfo

        return if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
            activeNetwork.isConnectedOrConnecting
        } else {
            false
        }
    }

    @JvmStatic
    fun showToastWarning(message : String?) {
        if (message == null)
            return
        val inflater =
            MyApplication.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.layout_toast, null)
        val image = layout.findViewById<ImageView>(R.id.image)
        image.setImageResource(R.drawable.ic_warning)
        val text = layout.findViewById<TextView>(R.id.text)
        text.text = message
        val toast = Toast(MyApplication.instance)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = LENGTH_LONG
        layout.setBackgroundColor(
            ContextCompat.getColor(
                MyApplication.instance,
                R.color.colorOrange
            )
        )
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        toast.view = layout
        toast.show()

    }

    @JvmStatic
    fun showToastInfo(message : String?) {
        if (message == null)
            return
        val inflater =
            MyApplication.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.layout_toast, null)
        val image = layout.findViewById<ImageView>(R.id.image)
        image.setImageResource(R.drawable.ic_info)
        val text = layout.findViewById<TextView>(R.id.text)
        text.setTextColor(MyApplication.instance.getColor(R.color.colorPrimary))
        text.text = message
        val toast = Toast(MyApplication.instance)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = LENGTH_LONG
        layout.setBackgroundColor(
            ContextCompat.getColor(
                MyApplication.instance,
                R.color.colorOrange
            )
        )
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        toast.view = layout
        toast.show()

    }

    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun addDatetoCurrentDate(add : Int) : String {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, add)
        return dateFormat.format(c.time)

    }

    @SuppressLint("SimpleDateFormat")
    fun getTodayDayName() : String {
        val sdf = SimpleDateFormat("EEE")
        val d = Date()
        val dayOfTheWeek = sdf.format(d)
        return dayOfTheWeek.toLowerCase()
    }

    fun getRandomColor() : String {
        val colors = ArrayList<String>()

        colors.add("#F366E0")
        colors.add("#F98D38")
        colors.add("#3A91E2")
        colors.add("#6FBA68")
        colors.add("#9FA8DA")
        colors.add("#DC4378")
        colors.add("#AED581")
        colors.add("#C155C8")
        colors.add("#ECC94A")
        colors.add("#4DD0E1")
        colors.add("#F3735A")
        colors.add("#31BFF0")
        val r = Random()
        val i = r.nextInt(11 - 0) + 0
        return colors[i]
    }

    @JvmStatic
    fun hideKeyBoard(view : View) {
        val imm = MyApplication.instance
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
