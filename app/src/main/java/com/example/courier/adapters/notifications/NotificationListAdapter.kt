package com.example.courier.adapters.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.courier.R
import com.example.courier.databinding.NotificationItemBinding
import com.example.courier.model.notifications.Data
import com.example.courier.model.order.OrdersListResponse
import com.example.courier.views.notifications.NotificationsFragment

class NotificationListAdapter(
    context : NotificationsFragment,
    addressList : ArrayList<Data>,
    var activity : Context
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    private val mContext : NotificationsFragment
    private var viewHolder : ViewHolder? = null
    private var orderList : ArrayList<Data>?

    init {
        this.mContext = context
        this.orderList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.notification_item,
            parent,
            false
        ) as NotificationItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, orderList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.txtOrderNo.text = orderList!![position].notificationTitle
        holder.binding!!.txtDetail.text = orderList!![position].notificationDescription
        holder.binding!!.txtDate.text = orderList!![position].createdAt

    }

    override fun getItemCount() : Int {
        return orderList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : NotificationItemBinding?,
        mContext : NotificationsFragment,
        addressList : ArrayList<Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}