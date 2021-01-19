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
import com.courierdriver.views.orders.TrackMapActivity

class NavigationAddressAdapter(
    var mOrderDetailsActivity: OrderDetailsActivity?,
    var mTrackMapActivity: TrackMapActivity?,
    var deliveryAddress: ArrayList<OrdersDetailResponse.PickupAddress>?
) : RecyclerView.Adapter<NavigationAddressAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_delivery_address,
            parent,
            false
        ) as RowDeliveryAddressBinding
        return ViewHolder(binding.root, viewType, binding, mOrderDetailsActivity,mTrackMapActivity, deliveryAddress)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.model = deliveryAddress!![position]

        if(mOrderDetailsActivity!=null) {
            holder.binding.relMain.setBackgroundColor(
                ContextCompat.getColor(
                    mOrderDetailsActivity!!,
                    R.color.colorWhite
                )
            )

            if (deliveryAddress!![position].isSelected)
                holder.binding.relMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mOrderDetailsActivity!!,
                        R.color.colorGreyLight
                    )
                )
            else
                holder.binding.relMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mOrderDetailsActivity!!,
                        R.color.colorWhite
                    )
                )
        }
        else
        {
            holder.binding.relMain.setBackgroundColor(
                ContextCompat.getColor(
                    mTrackMapActivity!!,
                    R.color.colorWhite
                )
            )

            if (deliveryAddress!![position].isSelected)
                holder.binding.relMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mTrackMapActivity!!,
                        R.color.colorGreyLight
                    )
                )
            else
                holder.binding.relMain.setBackgroundColor(
                    ContextCompat.getColor(
                        mTrackMapActivity!!,
                        R.color.colorWhite
                    )
                )
        }
    }

    override fun getItemCount(): Int {
        return deliveryAddress!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowDeliveryAddressBinding?,
        mOrderDetailsActivity: OrderDetailsActivity?,
        var mTrackMapActivity: TrackMapActivity?,
        orderList: ArrayList<OrdersDetailResponse.PickupAddress>?
    ) : RecyclerView.ViewHolder(v) {
        var lastChecked = 0

        init {

            binding!!.relMain.setOnClickListener {
                for (i in 0 until orderList!!.size) {
                    orderList[i].isSelected = false
                    // notifyDataSetChanged()
                }
                if(mOrderDetailsActivity!=null) {
                    binding.relMain.setBackgroundColor(
                        ContextCompat.getColor(
                            mOrderDetailsActivity,
                            R.color.colorGreyLight
                        )
                    )
                }
                else{
                    binding.relMain.setBackgroundColor(
                        ContextCompat.getColor(
                            mTrackMapActivity!!,
                            R.color.colorGreyLight
                        )
                    )
                }
                orderList[adapterPosition].isSelected = true
                orderList[adapterPosition].isComplete = true
                if(mOrderDetailsActivity!=null)
                    mOrderDetailsActivity.navigateIcon(orderList[adapterPosition])
                else
                    mTrackMapActivity!!.navigateIcon(orderList[adapterPosition])
                notifyDataSetChanged()
            }
        }
    }
}
