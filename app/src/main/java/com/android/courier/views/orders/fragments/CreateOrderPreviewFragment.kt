package com.android.courier.views.orders.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
                            showPaymentSuccessDialog()
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })


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

    public fun preFillData() {
        /*val txtDeliveryOption = confirmationDialog?.findViewById<TextView>(R.id.txtDeliveryOption)
        val txtFare = confirmationDialog?.findViewById<TextView>(R.id.txtFare)
        val txtWeight = confirmationDialog?.findViewById<TextView>(R.id.txtWeight)
        val txtValue = confirmationDialog?.findViewById<TextView>(R.id.txtValue)
        val txtFareCollected = confirmationDialog?.findViewById<TextView>(R.id.txtFareCollected)
        val txtVehicleType = confirmationDialog?.findViewById<TextView>(R.id.txtVehicleType)
        val btnConfirm = confirmationDialog?.findViewById<Button>(R.id.btnConfirm)
        val imgBack = confirmationDialog?.findViewById<ImageView>(R.id.imagBack)
        val imgUser = confirmationDialog?.findViewById<ImageView>(R.id.img_right)
        val rvAddress = confirmationDialog?.findViewById<RecyclerView>(R.id.rvAddress)*/
        /*  val layoutBottomSheet =
              confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)
          val animation = AnimationUtils.loadAnimation(this!!, R.anim.anim)
          animation.setDuration(500)
          layoutBottomSheet?.setAnimation(animation)
          layoutBottomSheet?.animate()
          animation.start()*/
        if (MyApplication.createOrdersInput.deliveryValue.equals("1")) {
            orderPreviewBinding.txtDeliveryOption.setText("Regular")
        } else {
            orderPreviewBinding.txtDeliveryOption.setText("Express")
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

        orderPreviewBinding.btnConfirm?.setOnClickListener {
            if (orderPreviewBinding.chkTermsAndPrivacy.isChecked == true) {
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
}