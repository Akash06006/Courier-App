package com.android.courier.adapters.orders

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.PaymentItemBinding
import com.android.courier.model.order.OrdersDetailResponse
import com.android.courier.views.orders.OrderDetailActivity

class PaymentOptionsListAdapter(
    context : OrderDetailActivity,
    addressList : ArrayList<OrdersDetailResponse.PayViaNew>?
) :
    RecyclerView.Adapter<PaymentOptionsListAdapter.ViewHolder>() {
    private val mContext : OrderDetailActivity
    private var viewHolder : ViewHolder? = null
    private var paymentList : ArrayList<OrdersDetailResponse.PayViaNew>?

    init {
        this.mContext = context
        this.paymentList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.payment_item,
            parent,
            false
        ) as PaymentItemBinding
        return ViewHolder(binding.root, viewType, binding/* mContext,*/)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder

        if (paymentList!![position].type!!.trim().equals("Paytm")) {
            holder.binding!!.imgPayment.setImageResource(R.drawable.ic_paytm)
        } else if (paymentList!![position].type!!.trim().contains("Google")) {
            holder.binding!!.imgPayment.setImageResource(R.drawable.ic_google_pay)
        } else if (paymentList!![position].type!!.trim().contains("Phone")) {
            holder.binding!!.imgPayment.setImageResource(R.drawable.ic_phone_pay)
        }

        holder.binding!!.imgPayment.setOnClickListener {
            showAlert(
                paymentList!![position].phoneNumber.toString(),
                paymentList!![position].type.toString()
            )
        }
    }

    //ic_phone_pay
    /* Paytm*/
    override fun getItemCount() : Int {
        return paymentList!!.size
    }

    fun showAlert(number : String, type : String) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(mContext),
                R.layout.layout_custom_alert,
                null,
                false
            )
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(type)
        // set the custom dialog components - text, image and button
        val text = dialog.findViewById(R.id.text) as TextView
        val title = dialog.findViewById(R.id.title) as TextView
        title.text = type
        text.text = number
        text.background = mContext.resources.getDrawable(R.drawable.shape_square_stroke)
        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        dialogButton.text = "Copy"
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {
            val textToCopy = text.text
            val clipboardManager =
                mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        dialog.show()

    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : PaymentItemBinding?
        /*mContext : BaseActivity,*/
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}