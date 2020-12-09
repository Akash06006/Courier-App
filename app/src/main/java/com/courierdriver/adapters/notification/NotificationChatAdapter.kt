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
import com.courierdriver.views.notification.ChatActivity
import com.courierdriver.views.notification.NotificationChatActivity

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

        if (datum.notificationType != null && datum.notificationType == "CHAT_NEW_MSG") {
            holder.binding.imgNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.ic_notification_chat
                )
            )
            holder.binding.tvReply.visibility = View.VISIBLE
        } else {
            holder.binding.imgNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.ic_notification
                )
            )
            holder.binding.tvReply.visibility = View.GONE
        }
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
                val intent = Intent(mContext, ChatActivity::class.java)
                intent.putExtra("model", notificationList!![adapterPosition])
                mContext.startActivity(intent)
            }
        }
    }
}