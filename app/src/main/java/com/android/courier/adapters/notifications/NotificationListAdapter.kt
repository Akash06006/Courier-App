package com.android.courier.adapters.notifications

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.NotificationItemBinding
import com.android.courier.model.notifications.Data
import com.android.courier.model.order.OrdersListResponse
import com.android.courier.views.chat.ChatActivity
import com.android.courier.views.chat.DriverChatActivity
import com.android.courier.views.notifications.NotificationsFragment
import com.android.courier.views.orders.OrderDetailActivity

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

        holder.binding.topLay.setOnClickListener {
            if (!TextUtils.isEmpty(orderList!![position].orderId)) {
                if (orderList!![position].notificationType.equals("DRIVER")) {
                    val intent = Intent(mContext.activity, DriverChatActivity::class.java)
                    intent.putExtra("orderId", orderList!![position].orderId)
                    intent.putExtra("driverId", orderList!![position].senderId)
                    mContext.startActivity(intent)
                } else if (orderList!![position].notificationType.equals("ADMIN")) {
                    val intent = Intent(mContext.activity, ChatActivity::class.java)
                    intent.putExtra("orderId", orderList!![position].orderId)
                    mContext.startActivity(intent)
                } else if (orderList!![position].notificationType.equals("ORDER")) {
                    var active = "true"
                    val intent = Intent(mContext.activity, OrderDetailActivity::class.java)
                    intent.putExtra("id", orderList!![position].orderId)
                    intent.putExtra("active", active)
                    mContext.startActivity(intent)
                }
            }
        }
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