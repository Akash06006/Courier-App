package com.courierdriver.adapters.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.databinding.RowDeliveryAddressBinding
import com.courierdriver.model.order.OrdersDetailResponse
import com.courierdriver.views.orders.OrderDetailsActivity

class NavigationAddressAdapter(
    var mContext: OrderDetailsActivity,
    var deliveryAddress: ArrayList<OrdersDetailResponse.PickupAddress>?
) :
    RecyclerView.Adapter<NavigationAddressAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_delivery_address,
            parent,
            false
        ) as RowDeliveryAddressBinding
        return ViewHolder(binding.root, viewType, binding, mContext, deliveryAddress)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.model = deliveryAddress!![position]

        holder.binding.relMain.setBackgroundColor(
            ContextCompat.getColor(
                mContext,
                R.color.colorWhite
            )
        )

        if (deliveryAddress!![position].isSelected)
            holder.binding.relMain.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorGreyLight
                )
            )
        else
            holder.binding.relMain.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorWhite
                )
            )
    }

    override fun getItemCount(): Int {
        return deliveryAddress!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowDeliveryAddressBinding?,
        mContext: OrderDetailsActivity,
        orderList: ArrayList<OrdersDetailResponse.PickupAddress>?
    ) : RecyclerView.ViewHolder(v) {
        var lastChecked = 0

        init {

            binding!!.relMain.setOnClickListener {
                for (i in 0 until orderList!!.size) {
                    orderList[i].isSelected = false
                    // notifyDataSetChanged()
                }
                binding.relMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.colorGreyLight
                    )
                )
                orderList[adapterPosition].isSelected = true
                orderList[adapterPosition].isComplete = true
                mContext.navigateIcon(orderList[adapterPosition])
                notifyDataSetChanged()
            }
        }
    }
}
