package com.courierdriver.views.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityPaymentButtonBinding
import com.courierdriver.model.PayUMoneyResponse
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.profile.ProfileViewModel
import com.google.gson.Gson
import com.payumoney.core.PayUmoneyConfig
import com.payumoney.core.PayUmoneyConstants
import com.payumoney.core.PayUmoneyConstants.RESULT
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat


class PaymentButtonActivity : BaseActivity() {
    private var binding: ActivityPaymentButtonBinding? = null
    private var viewModel: ProfileViewModel? = null
    var PAYMENT_CODE = 857
    private var payableAmount = "0"

    override fun initViews() {
        binding = viewDataBinding as ActivityPaymentButtonBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.viewModel = viewModel

        setToolbarTextIcons()
        getIntentData()
        viewClicks()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_pay" -> {
                        makePayment()
                    }
                }
            })
        )
    }

    private fun getIntentData() {
        payableAmount = intent.getStringExtra("amount").toString()
        payableAmount = DecimalFormat("##.##").format(payableAmount.toDouble())
        binding!!.price = payableAmount
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("paymentId", "0")
        setResult(PAYMENT_CODE, intent)
        finish()
    }

    //region PAYMENT
    private fun makePayment() {
        val payUmoneyConfig = PayUmoneyConfig.getInstance()
        payUmoneyConfig.payUmoneyActivityTitle =
            resources.getString(R.string.pay_to_locomo)
        payUmoneyConfig.doneButtonText =
            "Pay " + GlobalConstants.CURRENCY_SIGN + " " + payableAmount

        val builder = PayUmoneySdkInitializer.PaymentParam.Builder()
        builder.setAmount(payableAmount)
            .setTxnId(System.currentTimeMillis().toString())
            .setPhone(GlobalConstants.MOBILE)
            .setProductName("LOCOMO")
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
            stringBuilder.append(params[PayUmoneyConstants.KEY] + "|")
            stringBuilder.append(params[PayUmoneyConstants.TXNID] + "|")
            stringBuilder.append(params[PayUmoneyConstants.AMOUNT] + "|")
            stringBuilder.append(params[PayUmoneyConstants.PRODUCT_INFO] + "|")
            stringBuilder.append(params[PayUmoneyConstants.FIRSTNAME] + "|")
            stringBuilder.append(params[PayUmoneyConstants.EMAIL] + "|")
            stringBuilder.append(params[PayUmoneyConstants.UDF1] + "|")
            stringBuilder.append(params[PayUmoneyConstants.UDF2] + "|")
            stringBuilder.append(params[PayUmoneyConstants.UDF3] + "|")
            stringBuilder.append(params[PayUmoneyConstants.UDF4] + "|")
            stringBuilder.append(params[PayUmoneyConstants.UDF5] + "||||||")
            //calculateHashInServer(mPaymentParams);

            stringBuilder.append(GlobalConstants.SALT)

            val hash = hashCal(stringBuilder.toString())
            mPaymentParams.setMerchantHash(hash)
            PayUmoneyFlowManager.startPayUMoneyFlow(
                mPaymentParams,
                this,
                R.style.AppTheme,
                true
            )
            // calculateHashInServer(mPaymentParams);
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            // mTxvBuy.setEnabled(true);
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
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                Log.i(
                    "TAG",
                    "Success - Payment ID : " + data!!
                )
                val str = java.lang.StringBuilder()
                val bundle: Bundle = data.extras!!
                if (bundle != null) {
                    val keys = bundle.keySet()
                    val it: Iterator<String> = keys.iterator()
                    while (it.hasNext()) {
                        val key = it.next()
                        str.append(key)
                        str.append(":")
                        str.append(bundle[key])
                        str.append("\n\r")
                    }
                    Log.e("res: ", str.toString())
                }
                val response =
                    data.getParcelableExtra(INTENT_EXTRA_TRANSACTION_RESPONSE) as TransactionResponse?
                val payUMoneyResponse = response!!.getPayuResponse()
                Log.e("res: ", payUMoneyResponse)

                val gson = Gson()

                val organisation = gson.fromJson(
                    payUMoneyResponse,
                    PayUMoneyResponse::class.java
                )
                if (organisation != null) {
                    val paymentId = organisation.result!!.paymentId
                    val intent = Intent()
                    Log.d("TAG","paymentId=--- $paymentId")
                    intent.putExtra("paymentId", paymentId)
                    setResult(PAYMENT_CODE, intent)
                    finish()
                }
                else{
                    showToastError("Payment Failed")
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("TAG", "failure")
                showDialogMessage("cancelled")
            } else if (resultCode == RESULT_FAILED) {
                Log.i("app_activity", "failure")
                if (data != null) {
                    if (data.getStringExtra(RESULT) == "cancel") {
                    } else {
                        showDialogMessage("failure")
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == RESULT_BACK) {
                Log.i("TAG", "User returned without login")
                showDialogMessage("User returned without login")
            } else {
                Log.i("TAG", "failure")
                showDialogMessage("cancelled")
            }
        } else {
            Log.i("TAG", "failure")
            showDialogMessage("Cancelled")
        }
    }

    private fun showDialogMessage(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Failed")
        builder.setMessage(message)
        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> dialog.dismiss() }
        builder.show()
    }
    //endregion

    private fun setToolbarTextIcons() {
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.pay_to_locomo)
        binding!!.toolbarCommon.toolbar.setImageResource(R.drawable.ic_back)
        binding!!.toolbarCommon.imgRight.visibility = View.GONE
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_payment_button
    }

}