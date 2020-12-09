package com.courierdriver.views.profile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.adapters.PaymentHistoryAdapter
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.databinding.ActivityPaymentHistoryBinding
import com.courierdriver.model.PaymentHistoryModel
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.HelpScreenViewModel
import java.text.SimpleDateFormat
import java.util.*

class PaymentHistoryActivity : BaseActivity() {
    private var binding: ActivityPaymentHistoryBinding? = null
    private var viewModel: HelpScreenViewModel? = null
    private var paymentList: ArrayList<PaymentHistoryModel.Body>? = null
    private var isSelected = ""
    private var startDate = ""
    private var endDate = ""

    override fun initViews() {
        binding = viewDataBinding as ActivityPaymentHistoryBinding?
        viewModel = ViewModelProviders.of(this).get(HelpScreenViewModel::class.java)
        binding!!.viewModel = viewModel

        getPaymentList()
        loaderObserver()
      //  viewClicks()
    }

/*
    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_from" -> {
                        isSelected = "from"
                        datePicker()
                    }
                    "tv_to_date" -> {
                        val fromDate = binding!!.tvFrom.text.toString()
                        if (TextUtils.isEmpty(fromDate))
                            UtilsFunctions.showToastWarning("Select From Data")
                        else {
                            isSelected = "to"
                            datePicker()
                        }
                    }
                }
            })
        )
    }
*/

/*
    private fun datePicker() {
        var strDate: String
        var strTime: String
        val cCallendar = Calendar.getInstance()
        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                strDate = sdf.format(calendar.time)
                // showToastSuccess(strDate)

                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->


                        if (isSelected == "from") {
                            binding!!.tvFrom.text = strDate
                            startDate = strDate
                        } else {
                            binding!!.tvToDate.text =   strDate
                            endDate = strDate
                        }
                    }, cCallendar.get(Calendar.HOUR_OF_DAY), cCallendar.get(Calendar.MINUTE), false
                )
                timePickerDialog.show()
            }
        */
/*val dpDialog = DatePickerDialog(this, date, cCallendar
            .get(Calendar.YEAR), cCallendar.get(Calendar.MONTH),
            cCallendar.get(Calendar.DAY_OF_MONTH)
        )
        if (isSelected == "from") {
            val toDate = sharedPrefClass.getPrefValue(MyApplication.instance, GlobalConstants.TO_DATE).toString()
            if (TextUtils.isEmpty(toDate))
                dpDialog.datePicker.maxDate = System.currentTimeMillis() - 86400000
            else {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(toDate)
                val dat = SimpleDateFormat("yyyy-MM-dd").format(date)
                dpDialog.datePicker.maxDate = SimpleDateFormat("yyyy-MM-dd").parse(dat).time - 86400000
            }
        } else {
            dpDialog.datePicker.maxDate = System.currentTimeMillis()
            val fromDate = sharedPrefClass.getPrefValue(MyApplication.instance, GlobalConstants.FROM_DATE).toString()
            if (!TextUtils.isEmpty(fromDate)) {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fromDate)
                val dat = SimpleDateFormat("yyyy-MM-dd").format(date)
                dpDialog.datePicker.minDate = SimpleDateFormat("yyyy-MM-dd").parse(dat).time + 86400000
            }
        }
        dpDialog.show()*//*

    }
*/

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }


    private fun getPaymentList() {
        viewModel!!.paymentHistory()
        viewModel!!.paymentHistoryData().observe(
            this,
            Observer<PaymentHistoryModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            paymentList = response.body
                            if (paymentList!!.isNotEmpty())
                                setPaymentHistoryAdapter()
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setPaymentHistoryAdapter() {
        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = PaymentHistoryAdapter(this, paymentList)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding!!.rvPayments.layoutManager = linearLayoutManager
        binding!!.rvPayments.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_payment_history
    }
}