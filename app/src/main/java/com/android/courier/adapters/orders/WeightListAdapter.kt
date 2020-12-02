package com.android.courier.adapters.orders

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.VehicleWeightItemBinding
import com.android.courier.model.order.ListsResponse
import com.android.courier.views.orders.CreateOrderActivty
import com.android.courier.views.orders.fragments.CreateOrderFirstFragment

class WeightListAdapter(
    context : CreateOrderFirstFragment,
    addressList : ArrayList<ListsResponse.WeightData>
) :
    RecyclerView.Adapter<WeightListAdapter.ViewHolder>() {
    private val mContext : CreateOrderFirstFragment
    private var viewHolder : ViewHolder? = null
    private var weightList : ArrayList<ListsResponse.WeightData>?

    init {
        this.mContext = context
        this.weightList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.vehicle_weight_item,
            parent,
            false
        ) as VehicleWeightItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, weightList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.txtName.text = weightList!![position].name
        if (!TextUtils.isEmpty(weightList!![position].selected) && weightList!![position].selected.equals(
                "true"
            )
        ) {
            holder.binding!!.toLayout.setBackground(mContext.resources.getDrawable(R.drawable.ic_weight_seleted))
            /*  holder.binding!!.toLayout.setBackgroundTintList(
                  mContext.getResources().getColorStateList(R.color.colorPrimary)
              )*/
            holder.binding.txtName.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        } else {
            holder.binding!!.toLayout.setBackground(mContext.resources.getDrawable(R.drawable.ic_weight_unseleted))
            holder.binding.txtName.setTextColor(mContext.resources.getColor(R.color.colorBlack))
            // holder.binding!!.toLayout.setBackground(mContext.resources.getColor(R.color.colorWhite))
            /*  holder.binding!!.toLayout.setBackgroundTintList(
                  ColorStateList.valueOf(
                      Color.parseColor(
                          "#ffffff"
                      )
                  )*//*mContext.getResources().getColorStateList(R.color.colorOrange)*//*
            )*/
        }
        holder.binding!!.toLayout.setOnClickListener {
            if (weightList!![position].selected.equals("true")) {
                mContext.selectedWeight(position, "false")
            } else {
                mContext.selectedWeight(position, "true")
            }
        }

    }

    override fun getItemCount() : Int {
        return weightList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : VehicleWeightItemBinding?,
        mContext : CreateOrderFirstFragment,
        addressList : ArrayList<ListsResponse.WeightData>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}