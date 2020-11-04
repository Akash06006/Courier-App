package com.courierdriver.adapters.orders

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
import com.courierdriver.R
import com.courierdriver.databinding.VehicleWeightItemBinding
import com.courierdriver.model.order.ListsResponse
import com.courierdriver.views.orders.CreateOrderActivty

class WeightListAdapter(
    context: CreateOrderActivty,
    addressList: ArrayList<ListsResponse.WeightData>,
    var activity: Context
) :
    RecyclerView.Adapter<WeightListAdapter.ViewHolder>() {
    private val mContext: CreateOrderActivty
    private var viewHolder: ViewHolder? = null
    private var weightList: ArrayList<ListsResponse.WeightData>?

    init {
        this.mContext = context
        this.weightList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.vehicle_weight_item,
            parent,
            false
        ) as VehicleWeightItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, weightList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        val dis = (position + 1) * 10
        holder.binding!!.txtName.text = weightList!![position].name
        if (!TextUtils.isEmpty(weightList!![position].selected) && weightList!![position].selected.equals(
                "true"
            )
        ) {
            // holder.binding!!.toLayout.setBackground(mContext.resources.getColor(R.color.colorPrimary))
            holder.binding!!.toLayout.setBackgroundTintList(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#6D60F8"
                    )
                )/*mContext.getResources().getColorStateList(R.color.colorOrange)*/
            )
            holder.binding.txtName.setTextColor(mContext.resources.getColor(R.color.colorWhite))
        } else {
            // holder.binding.topLay.setBackgroundResource(R.drawable.shape_round_corner)
            holder.binding.txtName.setTextColor(mContext.resources.getColor(R.color.colorBlack))
            // holder.binding!!.toLayout.setBackground(mContext.resources.getColor(R.color.colorWhite))
            holder.binding!!.toLayout.setBackgroundTintList(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#ffffff"
                    )
                )/*mContext.getResources().getColorStateList(R.color.colorOrange)*/
            )
        }
        holder.binding!!.toLayout.setOnClickListener {
            if (weightList!![position].selected.equals("true")) {
                mContext.selectedWeight(position, "false")
            } else {
                mContext.selectedWeight(position, "true")
            }
        }

    }

    override fun getItemCount(): Int {
        return weightList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: VehicleWeightItemBinding?,
        mContext: CreateOrderActivty,
        addressList: ArrayList<ListsResponse.WeightData>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}