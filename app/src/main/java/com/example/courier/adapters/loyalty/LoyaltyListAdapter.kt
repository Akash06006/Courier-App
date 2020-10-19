package com.example.courier.adapters.loyalty

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.courier.R
import com.example.courier.databinding.LoyaltyItemBinding
import com.example.courier.model.loyalty.User
import com.example.courier.model.notifications.Data
import com.example.courier.views.refer.LoyaltyPointsListActivity

class LoyaltyListAdapter(
    context : LoyaltyPointsListActivity,
    addressList : ArrayList<User>,
    var activity : Context
) :
    RecyclerView.Adapter<LoyaltyListAdapter.ViewHolder>() {
    private val mContext : LoyaltyPointsListActivity
    private var viewHolder : ViewHolder? = null
    private var orderList : ArrayList<User>?

    init {
        this.mContext = context
        this.orderList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.loyalty_item,
            parent,
            false
        ) as LoyaltyItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, orderList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.txtUserName.text =
            orderList!![position].name
        if (orderList!![position].type.equals("earned")) {
            holder.binding!!.txtPoints.text = "+" + orderList!![position].points
            holder.binding!!.txtPoints.setTextColor(mContext.resources.getColor(R.color.colorSuccess))
            holder.binding!!.txtDetail.text =
                " Loyalty points credited : " + orderList!![position].points
        } else {
            holder.binding!!.txtPoints.text = "-" + orderList!![position].points
            holder.binding!!.txtPoints.setTextColor(mContext.resources.getColor(R.color.colorRed))
            holder.binding!!.txtDetail.text =
                " Loyalty points debited : " + orderList!![position].points
        }


        holder.binding!!.txtDate.text =
            orderList!![position].date
        Glide.with(mContext).load(orderList!![position].image).placeholder(R.drawable.ic_dummy)
            .into(holder.binding!!.imgUser)

    }

    override fun getItemCount() : Int {
        return orderList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : LoyaltyItemBinding?,
        mContext : LoyaltyPointsListActivity,
        addressList : ArrayList<User>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}