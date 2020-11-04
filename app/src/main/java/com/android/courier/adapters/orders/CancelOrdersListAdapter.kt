package com.android.courier.adapters.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.CancelOrderDetailBinding
import com.android.courier.model.order.Orders
import com.android.courier.views.orders.CreateOrderActivty

class CancelOrdersListAdapter(
    context : CreateOrderActivty,
    addressList : ArrayList<Orders>?,
    createOrderActivty : CreateOrderActivty
) :
    RecyclerView.Adapter<CancelOrdersListAdapter.ViewHolder>() {
    private val mContext : CreateOrderActivty
    private var viewHolder : ViewHolder? = null
    private var cancelOrdersList : ArrayList<Orders>?

    init {
        this.mContext = context
        this.cancelOrdersList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cancel_order_detail,
            parent,
            false
        ) as CancelOrderDetailBinding
        return ViewHolder(binding.root, viewType, binding, mContext, cancelOrdersList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.txtOrderNumber.text = cancelOrdersList!![position].orderNo
        holder.binding!!.txtItemName.text = cancelOrdersList!![position].itemName
        holder.binding!!.txtTotalPrice.text = cancelOrdersList!![position].totalOrderPrice + "₹"
        holder.binding!!.txtCancelPrice.text = cancelOrdersList!![position].cancelCharges + "₹"
    }

    override fun getItemCount() : Int {
        return cancelOrdersList!!.size
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : CancelOrderDetailBinding?,
        mContext : CreateOrderActivty,
        addressList : ArrayList<Orders>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}