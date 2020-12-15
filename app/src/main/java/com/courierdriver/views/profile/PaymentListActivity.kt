package com.courierdriver.views.profile

import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.databinding.ActivityPaymentListBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.PaymentOptionsModel
import com.courierdriver.utils.BaseFragment
import com.courierdriver.viewmodels.profile.ProfileViewModel

class PaymentListActivity : BaseFragment() {
    private var binding: ActivityPaymentListBinding? = null
    private var viewModel: ProfileViewModel? = null
    private var isPaytmVisible = false
    private var isPhonePeVisible = false
    private var isGooglePayVisible = false
    private var count = 0
    private var paymentType = ""
    private var paymentListData: ArrayList<PaymentOptionsModel.Body>? = null
    private var paytm = ""
    private var googlePay = ""
    private var phonePe = ""

    override fun initView() {
        binding = viewDataBinding as ActivityPaymentListBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.viewModel = viewModel
        clickViews()
        loaderObserver()
        submitPaymentObserver()
        paymentOptionsObserver()
    }

    private fun paymentOptionsObserver() {
        viewModel!!.paymentOptions()
        viewModel!!.paymentOptionsData().observe(this,
            Observer<PaymentOptionsModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            //   baseActivity.showToastSuccess(response.message)
                            binding!!.model = response
                            paymentListData = response.body
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

    private fun submitPaymentObserver() {
        viewModel!!.profileSetupListData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            baseActivity.showToastSuccess("Payments options updated successfully")
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

    private fun showError(textView: EditText, error: String) {
        textView.requestFocus()
        textView.error = error
    }

    private fun clickViews() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_invite_friend" -> {
                        count = 0
                        paymentType = ""
                        paytm = ""
                        googlePay = ""
                        phonePe = ""

                        if (!TextUtils.isEmpty(binding!!.etPhonePaytm.text.toString())) {
                            count += 1
                            paytm = paymentListData!![0].type!! + ","
                        }
                        if (!TextUtils.isEmpty(binding!!.etPhoneGooglePay.text.toString())) {
                            count += 1
                            googlePay = paymentListData!![1].type!! + ","
                        }
                        if (!TextUtils.isEmpty(binding!!.etPhonePhonePe.text.toString())) {
                            count += 1
                            phonePe = paymentListData!![2].type!!
                        }
                        if (!TextUtils.isEmpty(binding!!.etPhonePaytm.text.toString())
                            && (binding!!.etPhonePaytm.text.toString()).length < 10
                        ) {
                            showError(
                                binding!!.etPhonePaytm,
                                getString(R.string.paytm) + " " + getString(R.string.phone_number)
                                        + " " + getString(R.string.phone_min)
                            )
                        } else if (!TextUtils.isEmpty(binding!!.etPhoneGooglePay.text.toString())
                            && (binding!!.etPhoneGooglePay.text.toString()).length < 10
                        ) {
                            showError(
                                binding!!.etPhoneGooglePay,
                                getString(R.string.google_pay) + " " + getString(R.string.phone_number)
                                        + " " + getString(R.string.phone_min)
                            )
                        } else if (!TextUtils.isEmpty(binding!!.etPhonePhonePe.text.toString())
                            && (binding!!.etPhonePhonePe.text.toString()).length < 10
                        ) {
                            showError(
                                binding!!.etPhonePhonePe,
                                getString(R.string.phone_pe) + " " + getString(R.string.phone_number)
                                        + " " + getString(R.string.phone_min)
                            )
                        } else {
                            if (count < 2)
                                UtilsFunctions.showToastError(getString(R.string.payment_options_error))
                            else {
                                paymentType = paytm + googlePay + phonePe

                                viewModel!!.profileSetup(
                                    binding!!.etPhoneGooglePay.text.toString(),
                                    binding!!.etPhonePhonePe.text.toString(),
                                    binding!!.etPhonePaytm.text.toString(),
                                    paymentType
                                )
                            }
                        }
                    }
                    "rel_phone_pe" -> {
                        if (isPhonePeVisible) {
                            isPhonePeVisible = false
                            binding!!.linPhonePe.visibility = View.GONE
                            binding!!.imgArrowPhonePe.setImageDrawable(
                                baseActivity.getDrawable(
                                    R.drawable.ic_small_arrow_down
                                )
                            )
                        } else {
                            isPhonePeVisible = true
                            binding!!.linPhonePe.visibility = View.VISIBLE
                            binding!!.imgArrowPhonePe.setImageDrawable(
                                baseActivity.getDrawable(
                                    R.drawable.ic_up_arrow
                                )
                            )
                        }
                    }
                    "rel_google_pay" -> {
                        if (isGooglePayVisible) {
                            isGooglePayVisible = false
                            binding!!.linGooglePay.visibility = View.GONE
                            binding!!.imgArrowGooglePay.setImageDrawable(
                                baseActivity.getDrawable(
                                    R.drawable.ic_small_arrow_down
                                )
                            )
                        } else {
                            isGooglePayVisible = true
                            binding!!.linGooglePay.visibility = View.VISIBLE
                            binding!!.imgArrowGooglePay.setImageDrawable(
                                baseActivity.getDrawable(
                                    R.drawable.ic_up_arrow
                                )
                            )
                        }
                    }
                    "rel_paytm" -> {
                        if (isPaytmVisible) {
                            isPaytmVisible = false
                            binding!!.linPaytm.visibility = View.GONE
                            binding!!.imgArrowPaytm.setImageDrawable(
                                baseActivity.getDrawable(
                                    R.drawable.ic_small_arrow_down
                                )
                            )
                        } else {
                            isPaytmVisible = true
                            binding!!.linPaytm.visibility = View.VISIBLE
                            binding!!.imgArrowPaytm.setImageDrawable(
                                baseActivity.getDrawable(
                                    R.drawable.ic_up_arrow
                                )
                            )
                        }
                    }
                }
            })
        )
    }

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_payment_list
    }

}
