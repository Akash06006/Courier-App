package com.uniongoods.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.views.chat.ChatActivity
import com.android.courier.databinding.BoatChatItemBinding

class BoatChatMessageListAdapter(
    context : ChatActivity,
    boatMessageList : ArrayList<String>
) :
    RecyclerView.Adapter<BoatChatMessageListAdapter.ViewHolder>() {
    private val mContext : ChatActivity
    private var viewHolder : ViewHolder? = null
    private var boatMessageList : ArrayList<String>

    init {
        this.mContext = context
        this.boatMessageList = boatMessageList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.boat_chat_item,
            parent,
            false
        ) as BoatChatItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, boatMessageList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.tvMessage.text = boatMessageList[position]
        holder.binding!!.tvMessage.setOnClickListener {
            mContext.onClickBoatMessage(boatMessageList[position])
        }
        if (position == boatMessageList.size - 1) {
            holder.binding!!.viewBoat.visibility = View.GONE
        }
    }

    override fun getItemCount() : Int {
        return boatMessageList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : BoatChatItemBinding?,
        mContext : ChatActivity,
        addressList : ArrayList<String>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}