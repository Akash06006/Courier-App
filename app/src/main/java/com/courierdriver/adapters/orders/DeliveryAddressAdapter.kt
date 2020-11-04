package com.courierdriver.adapters.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.databinding.RowDeliveryAddressBinding
import com.courierdriver.model.order.OrderListModel
import com.courierdriver.views.home.fragments.HomeFragment

class DeliveryAddressAdapter(
    var mContext: HomeFragment,
    var deliveryAddress: ArrayList<OrderListModel.Body.DeliveryAddress>?,
    var orderStatus: Int
) : RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder>() {

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
    }

    override fun getItemCount(): Int {
        return deliveryAddress!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowDeliveryAddressBinding?,
        mContext: HomeFragment,
        orderList: ArrayList<OrderListModel.Body.DeliveryAddress>?
    ) : RecyclerView.ViewHolder(v) {
        init {
        }
    }
}
