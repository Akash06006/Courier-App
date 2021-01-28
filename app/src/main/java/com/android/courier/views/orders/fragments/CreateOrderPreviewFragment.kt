package com.android.courier.views.orders.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.orders.AddressListAdapter
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.databinding.ActivityOrderPreviewBinding
import com.android.courier.model.order.CreateOrderResponse
import com.android.courier.utils.BaseFragment
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.orders.CreateOrderActivty
import com.android.courier.views.orders.OrderDetailActivity

class
CreateOrderPreviewFragment : BaseFragment() {
    private lateinit var orderViewModel : OrderViewModel
    private lateinit var orderPreviewBinding : ActivityOrderPreviewBinding

    //var categoriesList = null
    var orderId = ""

    override fun getLayoutResId() : Int {
        return R.layout.activity_order_preview
    }

    override fun onResume() {
        super.onResume()
    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        orderPreviewBinding = viewDataBinding as ActivityOrderPreviewBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        orderPreviewBinding.orderViewModel = orderViewModel
        // categoriesList=List<Service>()
        preFillData()

        orderViewModel.createOrderRes().observe(this,
            Observer<CreateOrderResponse> { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            orderId = response.data?.id.toString()
                            // if (paymentType.equals("2")) {
                            // showPaymentSuccessDialog()
                            if (!TextUtils.isEmpty(MyApplication.createOrdersInput.orderId) && !MyApplication.createOrdersInput.orderId.equals(
                                    "null"
                                )
                            ) {
                                activity!!.finish()
                            } else {
                                val intent = Intent(activity, OrderDetailActivity::class.java)
                                intent.putExtra("id", orderId)
                                // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                activity!!.finish()
                            }
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })


        orderPreviewBinding.chkTermsAndPrivacy.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
                (activity as CreateOrderActivty).previewTick(isChecked)
            }
        })
        orderPreviewBinding.txtTermsAndPrivacy.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                Toast.makeText(activity!!, "Coming Soon", Toast.LENGTH_SHORT)
                    .show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(activity!!, "Coming Soon", Toast.LENGTH_SHORT)
                    .show()
            })
        )

        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "toolbar" -> {
                        /*val ss = SpannableString("Android is a Software stack")
                        val clickableSpan : ClickableSpan = object : ClickableSpan() {
                            override fun onClick(textView : View?) {
                                startActivity(Intent(activity, NextActivity::class.java))
                            }

                            override fun updateDrawState(ds : TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                            }
                        }
                        ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        val textView = findViewById(R.id.hello) as TextView
                        textView.text = ss
                        textView.movementMethod = LinkMovementMethod.getInstance()
                        textView.highlightColor = Color.TRANSPARENT*/
                    }
                }
            })
        )
    }

    fun preFillData() {
        if (MyApplication.createOrdersInput.deliveryType.equals("1")) {
            orderPreviewBinding.txtDeliveryOption.text = "Regular"
        } else {
            orderPreviewBinding.txtDeliveryOption.text = "Express"
        }

        orderPreviewBinding.txtFare.setText(MyApplication.createOrdersInput.orderPrice)
        orderPreviewBinding.txtWeight.setText(MyApplication.createOrdersInput.weightValue)
        orderPreviewBinding.txtValue.setText(MyApplication.createOrdersInput.parcelValue)
        orderPreviewBinding.txtFareCollected.setText(MyApplication.createOrdersInput.fareCollected)
        orderPreviewBinding.txtItemName.setText(MyApplication.createOrdersInput.itemName)
        //txtVehicleType.setText(vehicleValue)
        val addressAdapter =
            AddressListAdapter(
                MyApplication.createOrdersInput.deliveryAddress,
                MyApplication.createOrdersInput.pickupAddress
            )
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_left)
        orderPreviewBinding.rvAddress.setLayoutAnimation(controller);
        orderPreviewBinding.rvAddress.scheduleLayoutAnimation();
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        orderPreviewBinding.rvAddress.layoutManager = linearLayoutManager
        orderPreviewBinding.rvAddress.setHasFixedSize(true)
        orderPreviewBinding.rvAddress.adapter = addressAdapter
        orderPreviewBinding.rvAddress.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
            }
        })
        /*  Glide.with(this).load(userImage).placeholder(R.drawable.ic_user).into(imgUser)
          imgBack?.setOnClickListener {
              confirmationDialog?.dismiss()
          }*/

        orderPreviewBinding.btnConfirm.setOnClickListener {
            if (orderPreviewBinding.chkTermsAndPrivacy.isChecked) {
                if (UtilsFunctions.isNetworkConnected()) {
                    baseActivity.startProgressDialog()
                    orderViewModel.createOrder(MyApplication.createOrdersInput)
                    //confirmationDialog?.dismiss()
                }
            } else {
                UtilsFunctions.showToastError("Please accept Terms & Conditions and Privacy Policy")
            }
        }
        // confirmationDialog?.show()
    }

    private fun showPaymentSuccessDialog() {
        val confirmationDialog = Dialog(activity, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.order_place_success_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = confirmationDialog?.findViewById<Button>(R.id.btnDone)
        cancel?.setOnClickListener {
            confirmationDialog?.dismiss()
            if (!TextUtils.isEmpty(MyApplication.createOrdersInput.orderId) && !MyApplication.createOrdersInput.orderId.equals(
                    "null"
                )
            ) {
                activity!!.finish()
            } else {
                val intent = Intent(activity, OrderDetailActivity::class.java)
                intent.putExtra("id", orderId)
                // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity!!.finish()
            }
        }
        confirmationDialog?.show()
    }

    fun TextView.makeLinks(vararg links : Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint : TextPaint) {
                    // use this to change the link color
                    textPaint.color = activity!!.resources.getColor(R.color.colorPrimary)
                    // textPaint.setStyle(Typeface.BOLD)
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    // textPaint.isUnderlineText = true
                }

                override fun onClick(view : View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}