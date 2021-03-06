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
import com.android.courier.views.orders.CreateOrderActivty

class AddressListAdapter(
    /* context : CreateOrderActivty,*/
    addressList : ArrayList<CreateOrdersInput.PickupAddress>?,
    pickupAddress : CreateOrdersInput.PickupAddress?/*,
    createOrderActivty : CreateOrderActivty*/
) :
    RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {
    //private val mContext : CreateOrderActivty
    private var viewHolder : ViewHolder? = null
    private var pickupAddress : CreateOrdersInput.PickupAddress?
    private var addressList : ArrayList<CreateOrdersInput.PickupAddress>?

    init {
        //this.mContext = context
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
        return ViewHolder(binding.root, viewType, binding/*, mContext, addressList*/)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        if (position == 0) {
            holder.binding!!.txtAddressType.text =
                "PickUp Location" //mContext.getString(R.string.pickup_address)
            holder.binding!!.txtAddress.text = pickupAddress?.address

        } else {
            if (position == 1) {
                holder.binding!!.txtAddressType.text =
                    "Drop Location"//mContext.getString(R.string.delivery_address)
            } else {
                holder.binding!!.txtAddressType.visibility = View.GONE
            }

            holder.binding!!.txtAddress.text = addressList!![position - 1].address
        }
    }

    override fun getItemCount() : Int {
        return addressList!!.size.plus(1)
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : AddressItemBinding?
        /* mContext : CreateOrderActivty,
        addressList : ArrayList<CreateOrdersInput.PickupAddress>?*/
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}