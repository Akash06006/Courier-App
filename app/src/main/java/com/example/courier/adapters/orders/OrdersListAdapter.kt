package com.example.courier.adapters.orders

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.courier.R
import com.example.courier.databinding.OrderItemBinding
import com.example.courier.model.order.OrdersListResponse
import com.example.courier.views.orders.OrderDetailActivity
import com.example.courier.views.orders.fragments.OrdersFragment

class OrdersListAdapter(
    context : OrdersFragment,
    addressList : ArrayList<OrdersListResponse.Data>,
    var activity : Context,
    active : String
) :
    RecyclerView.Adapter<OrdersListAdapter.ViewHolder>() {
    private val mContext : OrdersFragment
    private var viewHolder : ViewHolder? = null
    private var active = ""
    private var orderList : ArrayList<OrdersListResponse.Data>?

    init {
        this.mContext = context
        this.orderList = addressList
        this.active = active
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.order_item,
            parent,
            false
        ) as OrderItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, orderList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        val dis = (position + 1) * 10
        if (active.equals("true")) {
            holder.binding!!.txtTrack.visibility = View.VISIBLE
            holder.binding!!.txtCancel.visibility = View.VISIBLE
            holder.binding!!.llActiveOrder.visibility = View.VISIBLE

            holder.binding!!.txtOrderStatus.visibility = View.GONE
            holder.binding!!.txtViewDetails.visibility = View.GONE
            holder.binding!!.llOrderCompleted.visibility = View.GONE

            holder.binding.txtOrderNo.text = "Order No #" + orderList!![position].orderNo
            holder.binding.txtKm.text =
                orderList!![position].deliveryPoints!![0].distance + " Away"
            holder.binding.txtTime.text = "In " + orderList!![position].deliveryPoints!![0].time
            holder.binding.txtStatus.text = orderList!![position].orderStatus
            holder.binding.txtCreatedTime.text = "Created on " + orderList!![position].createdAt

            holder.binding.txtPickAddress.text = orderList!![position].pickupAddress?.address
            if (orderList!![position].deliveryAddress?.size!! > 1) {
                val size = orderList!![position].deliveryAddress?.size!!
                holder.binding.txtDeliveryAddress.text =
                    orderList!![position].deliveryAddress!![size?.minus(1)].address + " and " + size?.minus(
                        1
                    ) + " address in between"
            } else {
                if (orderList!![position].deliveryAddress?.size!! > 0)
                    holder.binding.txtDeliveryAddress.text =
                        orderList!![position].deliveryAddress!![0].address
            }

        } else {
            holder.binding!!.txtTrack.visibility = View.GONE
            holder.binding!!.txtCancel.visibility = View.GONE
            holder.binding!!.llActiveOrder.visibility = View.GONE

            holder.binding!!.txtOrderStatus.visibility = View.VISIBLE
            holder.binding!!.txtViewDetails.visibility = View.VISIBLE
            holder.binding!!.llOrderCompleted.visibility = View.VISIBLE

            holder.binding.txtOrderNo.text = "Order No #" + orderList!![position].orderNo
            holder.binding.txtOrderStatus.text = orderList!![position].orderStatus
            holder.binding.txtDistance.text = orderList!![position].weight?.name

        }
        //holder.binding!!.txtName.text = weightList!![position].name
        holder.binding!!.txtTrack.setOnClickListener {
        }
        holder.binding!!.txtCancel.setOnClickListener {
            mContext.showCancelReasonDialog(
                orderList!![position].id!!,
                orderList!![position].cancellationCharges
            )
        }

        holder.binding!!.topLay.setOnClickListener {
            val intent = Intent(mContext.activity, OrderDetailActivity::class.java)
            intent.putExtra("id", orderList!![position].id)
            intent.putExtra("active", active)
            mContext.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return orderList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : OrderItemBinding?,
        mContext : OrdersFragment,
        addressList : ArrayList<OrdersListResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}