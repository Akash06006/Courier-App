package com.android.courier.adapters.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.PaymentItemBinding
import com.android.courier.views.orders.OrderDetailActivity

class PaymentOptionsListAdapter(
    context : OrderDetailActivity,
    addressList : ArrayList<String>?
) :
    RecyclerView.Adapter<PaymentOptionsListAdapter.ViewHolder>() {
    private val mContext : OrderDetailActivity
    private var viewHolder : ViewHolder? = null
    private var paymentList : ArrayList<String>?

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
        val dis = (position + 1) * 10
        if (paymentList!![position].equals("Google Pay")) {
            holder.binding!!.imgPayment.setImageResource(R.drawable.ic_google_pay)
        } else if (paymentList!![position].equals("Patym") || paymentList!![position].equals("Paytm")) {
            holder.binding!!.imgPayment.setImageResource(R.drawable.ic_paytm)
        } else {
            holder.binding!!.imgPayment.setImageResource(R.drawable.ic_cash)
        }
    }

    override fun getItemCount() : Int {
        return paymentList!!.size
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