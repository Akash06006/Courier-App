package com.courierdriver.adapters.notification

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.databinding.RowNotificationChatBinding
import com.courierdriver.model.NotificationListModel
import com.courierdriver.utils.BaseActivity
import com.courierdriver.views.chat.CustomerChatActivity
import com.courierdriver.views.orders.OrderDetailsActivity

class NotificationChatAdapter(
    var mContext: BaseActivity,
    var notificationList: List<NotificationListModel.Body>?
) : RecyclerView.Adapter<NotificationChatAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_notification_chat,
            parent,
            false
        ) as RowNotificationChatBinding
        return ViewHolder(binding.root, viewType, binding, mContext, notificationList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        val datum = notificationList!![position]
        holder.binding!!.model = datum

        when (datum.notificationType) {
            "CUSTOMER" -> {
                setChatIcon(holder.binding)
            }
            "ADMIN" -> {
                setChatIcon(holder.binding)
            }
            else -> {
                setNotificationIcon(holder.binding)
            }
        }

    }

    private fun setNotificationIcon(binding: RowNotificationChatBinding) {
        binding.imgNotification.setImageDrawable(
            ContextCompat.getDrawable(
                mContext,
                R.drawable.ic_notification
            )
        )
        binding.tvReply.visibility = View.GONE
    }

    private fun setChatIcon(binding: RowNotificationChatBinding) {
        binding.imgNotification.setImageDrawable(
            ContextCompat.getDrawable(
                mContext,
                R.drawable.ic_notification_chat
            )
        )
        binding.tvReply.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return notificationList!!.size
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowNotificationChatBinding?,
        val mContext: BaseActivity,
        var notificationList: List<NotificationListModel.Body>?
    ) : RecyclerView.ViewHolder(v) {
        init {
            binding!!.tvReply.setOnClickListener {
                when (notificationList!![adapterPosition].notificationType) {
                    "CUSTOMER" -> {
                        val intent = Intent(mContext, CustomerChatActivity::class.java)
                        intent.putExtra("cust_id", notificationList!![adapterPosition].userId)
                        intent.putExtra("orderId", notificationList!![adapterPosition].orderId)
                        mContext.startActivity(intent)
                    }
                    "ADMIN" -> {
                        val intent =
                            Intent(mContext, com.courierdriver.views.chat.ChatActivity::class.java)
                        intent.putExtra("orderId", notificationList!![adapterPosition].orderId)
                        mContext.startActivity(intent)
                    }
                }
            }

            binding.cvOrderList.setOnClickListener {
                if (notificationList!![adapterPosition].notificationType == "ORDER") {
                    val intent = Intent(mContext, OrderDetailsActivity::class.java)
                    intent.putExtra("id", notificationList!![adapterPosition].orderId)
                    mContext.startActivity(intent)
                }

            }
        }
    }
}