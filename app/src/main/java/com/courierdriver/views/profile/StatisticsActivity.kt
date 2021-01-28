package com.courierdriver.views.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.adapters.profile.StatisticsAdapter
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityStatisticsBinding

import com.courierdriver.model.CommonModel
import com.courierdriver.model.StatisticsModel
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DialogssInterface
import com.courierdriver.utils.Utils
import com.courierdriver.viewmodels.profile.ProfileViewModel
import com.payumoney.core.PayUmoneyConfig
import com.payumoney.core.PayUmoneyConstants.*
import com.payumoney.core.PayUmoneySdkInitializer.PaymentParam
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class StatisticsActivity : BaseFragment(), DialogssInterface {
    private var binding: ActivityStatisticsBinding? = null
    private var viewModel: ProfileViewModel? = null
    private var monthStatisticsList = ArrayList<StatisticsModel.Body.Year>()
    private var monthName = "All"
    private var monthList = ArrayList<String>()
    private var yearsList = ArrayList<String>()
    private var invitedPoints = 0.0
    private var earnedPoints = 0.0
    private var payableAmount = "0"
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var TAG =
        "mainActivity"
    var PAYMENT_CODE = 857
    private var selectedMonth = "1"
    private var selectedYear = "2020"
    private var selectedWeek = "1"
    private var additionalChargesData: StatisticsModel.Body.CommissionData? = null
    private var totalComissionAddCharges = 0.0

    override fun initView() {
        binding = viewDataBinding as ActivityStatisticsBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.currencySign = GlobalConstants.CURRENCY_SIGN
        monthList = Utils(context!!).monthList()
        yearsList = Utils(context!!).yearsList()
        setMonthsSpinner()
        setYearsSpinner()
        getProfileDetailsObserver()
        payComissionObserver()
        loaderObserver()
        viewClicks()
        setWeeksSpinner()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_pay_now" -> {
                        //  makePayment()
                        if (!TextUtils.isEmpty(payableAmount) && payableAmount.toDouble() > 0) {
//                            binding!!.tvPayNow.isEnabled = true
                            if (earnedPoints > payableAmount.toDouble()) {
                                viewModel!!.payComission(
                                    "0",
                                    payableAmount,
                                    additionalChargesData!!.additionalCharges!!.securityFee,
                                    additionalChargesData!!.additionalCharges!!.cancelChargesCust,
                                    additionalChargesData!!.additionalCharges!!.cancellationCharges,
                                    additionalChargesData!!.usedCash,
                                    additionalChargesData!!.usedPoints
                                )
                            } else {
                                val intent = Intent(baseActivity, PaymentButtonActivity::class.java)
                                intent.putExtra("amount", payableAmount)
                                startActivityForResult(intent, PAYMENT_CODE)

/*
                                viewModel!!.payComission(
                                    "0",
                                    payableAmount,
                                    additionalChargesData!!.additionalCharges!!.securityFee,
                                    additionalChargesData!!.additionalCharges!!.cancelChargesCust,
                                    additionalChargesData!!.additionalCharges!!.cancellationCharges,
                                    additionalChargesData!!.usedCash,
                                    additionalChargesData!!.usedPoints
                                )
*/

                            }
                        } /*else if (totalComissionAddCharges > 0) {
                            viewModel!!.payComission(
                                "0",
                                totalComissionAddCharges.toString(),
                                additionalChargesData!!.additionalCharges!!.securityFee,
                                additionalChargesData!!.additionalCharges!!.cancelChargesCust,
                                additionalChargesData!!.additionalCharges!!.cancellationCharges,
                                additionalChargesData!!.usedCash,
                                additionalChargesData!!.usedPoints
                            )
                        }*//* else {
                            binding!!.tvPayNow.isEnabled = false
                        }*/

                    }
                    "tv_convert_to_cash" -> {
                        val count = invitedPoints + earnedPoints
                        if (count > 0) {
                            confirmationDialog = mDialogClass.setDefaultDialog(
                                baseActivity,
                                this,
                                "logout",
                                "Convert to cash",
                                getString(R.string.convert_to_cash_alert),
                                getString(R.string.convert_to_cash_alert)
                            )
                            confirmationDialog!!.show()
                        } else {
                            UtilsFunctions.showToastError(getString(R.string.enough_points_error))
                        }
                    }
                }
            })
        )
    }

    //region DIALOG
    override fun onDialogConfirmAction(mView: View?, mKey: String?) {
        viewModel!!.convertPoints()
        convertPointsObserver()
    }

    override fun onDialogCancelAction(mView: View?, mKey: String?) {
        confirmationDialog!!.dismiss()
    }
    //endregion

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(baseActivity)
        val adapter = StatisticsAdapter(baseActivity, monthStatisticsList, monthName)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding!!.rvMonthlyList.layoutManager = linearLayoutManager
        binding!!.rvMonthlyList.adapter = adapter
    }

    private fun payComissionObserver() {
        viewModel!!.payComissionData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            baseActivity.showToastSuccess("Payment successfull")
                            viewModel!!.statistics(
                                GlobalConstants.STATISTICS,
                                selectedYear,
                                selectedMonth, selectedWeek
                            )
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }


    private fun getProfileDetailsObserver() {
        viewModel!!.statistics(
            GlobalConstants.STATISTICS,
            selectedYear,
            selectedMonth,
            selectedWeek
        )
        viewModel!!.statisticsData().observe(this,
            Observer<StatisticsModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            val data = response.body
                            data?.let {
                                binding!!.model = data
                                invitedPoints =
                                    data.pointsData!!.invitedFriends!!.toDouble()
                                earnedPoints = data.pointsData.earnedPoints!!.toDouble()
                                payableAmount = data.commissionData!!.payableCommission!!
                                monthStatisticsList = data.year!!
                                additionalChargesData = data.commissionData
                                val additionalCharges =
                                    additionalChargesData!!.additionalCharges!!.cancellationCharges!!.toDouble() +
                                            additionalChargesData!!.additionalCharges!!.cancelChargesCust!!.toDouble() + additionalChargesData!!.additionalCharges!!.securityFee!!.toDouble()
                                binding!!.additionalCharges = additionalCharges.toString()

                                totalComissionAddCharges =
                                    data.commissionData.totalCommission!!.toDouble() + additionalCharges

                                val count = invitedPoints + earnedPoints
                                if (count <= 0) {
                                    binding!!.tvConvertToCash.backgroundTintList =
                                        ContextCompat.getColorStateList(
                                            baseActivity,
                                            R.color.colorGrey
                                        )
                                } else {
                                    binding!!.tvConvertToCash.backgroundTintList =
                                        ContextCompat.getColorStateList(
                                            baseActivity,
                                            R.color.colorPrimary
                                        )
                                }

                                if (payableAmount.toDouble() <= 0) {
                                    binding!!.tvPayNow.backgroundTintList =
                                        ContextCompat.getColorStateList(
                                            baseActivity,
                                            R.color.colorGrey
                                        )
                                } else {
                                    binding!!.tvPayNow.backgroundTintList =
                                        ContextCompat.getColorStateList(
                                            baseActivity,
                                            R.color.colorPrimary
                                        )
                                }
                                setAdapter()
                            }
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun convertPointsObserver() {
        viewModel!!.convertPoints()
        viewModel!!.convertPointsData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (confirmationDialog != null)
                    confirmationDialog!!.dismiss()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            invitedPoints = 0.0
                            earnedPoints = 0.0
                            UtilsFunctions.showToastSuccess(response.message!!)
                            viewModel!!.statistics(
                                GlobalConstants.STATISTICS,
                                selectedYear,
                                selectedMonth,
                                selectedWeek
                            )
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setYearsSpinner() {
        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
        adapter.addAll(yearsList)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding!!.spYears.adapter = adapter

        binding!!.spYears.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selectedYear = yearsList[position]

                viewModel!!.statistics(
                    GlobalConstants.STATISTICS,
                    selectedYear,
                    selectedMonth,
                    selectedWeek
                )
                //  setAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun setMonthsSpinner() {
        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
        adapter.addAll(monthList)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding!!.spMonths.adapter = adapter

        binding!!.spMonths.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val pos = position + 1
                selectedMonth = pos.toString()

                viewModel!!.statistics(
                    GlobalConstants.STATISTICS,
                    selectedYear,
                    selectedMonth,
                    selectedWeek
                )
                //  setAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun setWeeksSpinner() {
        val weeksList = ArrayList<String>()
        weeksList.add("Today")
        weeksList.add("Last 7 days")
        weeksList.add("Last 15 days")
        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
        adapter.addAll(weeksList)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding!!.spWeeks.adapter = adapter

        binding!!.spWeeks.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selectedWeek = position.toString()

                when (position) {
                    0 -> {
                        selectedWeek = "1"
                    }
                    1 -> {
                        selectedWeek = "2"
                    }
                    2 -> {
                        selectedWeek = "3"
                    }
                }
                viewModel!!.statistics(
                    GlobalConstants.STATISTICS,
                    selectedYear,
                    selectedMonth,
                    selectedWeek
                )
                //  setAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    //region PAYMENT
    private fun makePayment() {
        val payUmoneyConfig = PayUmoneyConfig.getInstance()
        payUmoneyConfig.payUmoneyActivityTitle =
            resources.getString(R.string.pay_to_locomo)
        payUmoneyConfig.doneButtonText =
            "Pay " + GlobalConstants.CURRENCY_SIGN + " " + payableAmount

        val builder = PaymentParam.Builder()
        builder.setAmount(payableAmount)
            .setTxnId(System.currentTimeMillis().toString())
            .setPhone(GlobalConstants.MOBILE)
            .setProductName(getString(R.string.app_name))
            .setFirstName(GlobalConstants.FIRST_NAME)
            .setEmail(GlobalConstants.PAYUMONEY_EMAIL)
            .setsUrl(GlobalConstants.SURL)
            .setfUrl(GlobalConstants.FURL)
            .setUdf1("as")
            .setUdf2("sad")
            .setUdf3("ud3")
            .setUdf4("")
            .setUdf5("")
            .setUdf6("")
            .setUdf7("")
            .setUdf8("")
            .setUdf9("")
            .setUdf10("")
            .setIsDebug(GlobalConstants.DEBUG)
            .setKey(GlobalConstants.MERCHANT_KEY)
            .setMerchantId(GlobalConstants.MERCHANT_ID)

        try {
            val mPaymentParams = builder.build()
            val stringBuilder = StringBuilder()
            val params = mPaymentParams.params
            stringBuilder.append(params[KEY] + "|")
            stringBuilder.append(params[TXNID] + "|")
            stringBuilder.append(params[AMOUNT] + "|")
            stringBuilder.append(params[PRODUCT_INFO] + "|")
            stringBuilder.append(params[FIRSTNAME] + "|")
            stringBuilder.append(params[EMAIL] + "|")
            stringBuilder.append(params[UDF1] + "|")
            stringBuilder.append(params[UDF2] + "|")
            stringBuilder.append(params[UDF3] + "|")
            stringBuilder.append(params[UDF4] + "|")
            stringBuilder.append(params[UDF5] + "||||||")
            //calculateHashInServer(mPaymentParams);

            stringBuilder.append(GlobalConstants.SALT)

            val hash = hashCal(stringBuilder.toString())
            mPaymentParams.setMerchantHash(hash)
            PayUmoneyFlowManager.startPayUMoneyFlow(
                mPaymentParams,
                baseActivity,
                R.style.AppTheme,
                true
            )
            // calculateHashInServer(mPaymentParams);
        } catch (e: Exception) {
            Toast.makeText(baseActivity, e.message, Toast.LENGTH_LONG).show()
            // mTxvBuy.setEnabled(true);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_CODE) {
            // baseActivity.showToastSuccess("Success")
            if (data != null) {
                if (data.hasExtra("paymentId")) {
                    val paymentId = data.getStringExtra("paymentId")
                    Log.d("TAG", "paymentIdStats=--- $paymentId")
                    if (paymentId != "0") {
                        viewModel!!.payComission(
                            paymentId,
                            payableAmount,
                            additionalChargesData!!.additionalCharges!!.securityFee,
                            additionalChargesData!!.additionalCharges!!.cancelChargesCust,
                            additionalChargesData!!.additionalCharges!!.cancellationCharges,
                            additionalChargesData!!.usedCash, additionalChargesData!!.usedPoints
                        )
                    }
                }
            }

        }
    }

    private fun hashCal(str: String): String? {
        val hashseq = str.toByteArray()
        val hexString = StringBuilder()
        try {
            val algorithm: MessageDigest = MessageDigest.getInstance("SHA-512")
            algorithm.reset()
            algorithm.update(hashseq)
            val messageDigest: ByteArray = algorithm.digest()
            for (aMessageDigest in messageDigest) {
                val hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
                if (hex.length == 1) {
                    hexString.append("0")
                }
                hexString.append(hex)
            }
        } catch (ignored: NoSuchAlgorithmException) {
        }
        return hexString.toString()
    }

    val RESULT_FAILED = 90
    val RESULT_BACK = 8

    private fun showDialogMessage(message: String) {
        val builder = AlertDialog.Builder(baseActivity)
        builder.setTitle(TAG)
        builder.setMessage(message)
        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> dialog.dismiss() }
        builder.show()
    }
    //endregion

    override fun getLayoutResId(): Int {
        return R.layout.activity_statistics
    }
}