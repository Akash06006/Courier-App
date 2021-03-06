package com.android.courier.adapters.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.AddressItemBinding
import com.android.courier.model.order.CreateOrdersInput
import com.android.courier.model.order.OrdersDetailResponse
import com.android.courier.views.orders.OrderDetailActivity

class OrderAddressListAdapter(
    context : OrderDetailActivity,
    addressList : ArrayList<OrdersDetailResponse.PickupAddress>?,
    pickupAddress : OrdersDetailResponse.PickupAddress?
) :
    RecyclerView.Adapter<OrderAddressListAdapter.ViewHolder>() {
    private val mContext : OrderDetailActivity
    private var viewHolder : ViewHolder? = null
    private var pickupAddress : OrdersDetailResponse.PickupAddress?
    private var addressList : ArrayList<OrdersDetailResponse.PickupAddress>?

    init {
        this.mContext = context
        this.addressList = addressList
        this.pickupAddress = pickupAddress
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.address_item,
            parent,
            false
        ) as AddressItemBinding
        return ViewHolder(binding.root, viewType, binding,/* mContext,*/ addressList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        val dis = (position + 1) * 10
        if (position == 0) {
            holder.binding!!.txtAddressType.text = mContext.getString(R.string.pickup_address)
            holder.binding!!.txtAddress.text = pickupAddress?.address

        } else {
            holder.binding!!.txtAddressType.text = mContext.getString(R.string.delivery_address)
            holder.binding!!.txtAddress.text = addressList!![position - 1].address
        }
    }

    override fun getItemCount() : Int {
        return addressList!!.size.plus(1)
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : AddressItemBinding?,
        /*mContext : BaseActivity,*/
        addressList : ArrayList<OrdersDetailResponse.PickupAddress>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}