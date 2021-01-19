package com.courierdriver.adapters.orders

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.databinding.DetailRowDeliveryAddressBinding
import com.courierdriver.model.order.OrdersDetailResponse
import com.courierdriver.views.orders.OrderDetailsActivity

class DetailDeliveryAddressAdapter(
    var mContext: OrderDetailsActivity,
    var deliveryAddress: ArrayList<OrdersDetailResponse.PickupAddress>?,
    var orderDetail: OrdersDetailResponse.Data?
) : RecyclerView.Adapter<DetailDeliveryAddressAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.detail_row_delivery_address,
            parent,
            false
        ) as DetailRowDeliveryAddressBinding
        return ViewHolder(binding.root, viewType, binding, mContext, deliveryAddress)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.model = deliveryAddress!![position]
        if (position == 0)
            holder.binding.tvDeliveryAddress.visibility = View.VISIBLE
        else
            holder.binding.tvDeliveryAddress.visibility = View.GONE

        orderDetail?.let { hideUnhideButtons(it, holder.binding) }
        if (deliveryAddress!![position].isComplete!!)
            holder.binding.imgCompleted.visibility = View.VISIBLE
        else
            holder.binding.imgCompleted.visibility = View.GONE
    }

    private fun hideUnhideButtons(
        data: OrdersDetailResponse.Data,
        binding: DetailRowDeliveryAddressBinding
    ) {
        val orderStatus = data.orderStatus?.status // 1 available, 2 active, 3 completed
        if (orderStatus.equals("1")) {
            binding.imgCall.visibility = View.GONE
        } else if (orderStatus.equals("2")) {
            binding.imgCall.visibility = View.VISIBLE
        } else if (orderStatus.equals("3")) {
            binding.imgCall.visibility = View.GONE
        } else if (orderStatus.equals("4")) {
            binding.imgCall.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        return deliveryAddress!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: DetailRowDeliveryAddressBinding?,
        mContext: OrderDetailsActivity,
        orderList: ArrayList<OrdersDetailResponse.PickupAddress>?
    ) : RecyclerView.ViewHolder(v) {
        init {
            binding!!.imgCall.setOnClickListener {
                if (orderList!![adapterPosition].phoneNumber != null) {
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:" + orderList!![adapterPosition].phoneNumber)
                    mContext.startActivity(dialIntent)
                }
            }


        }
    }
}
