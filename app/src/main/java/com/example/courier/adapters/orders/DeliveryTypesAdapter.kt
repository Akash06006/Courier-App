package com.example.courier.adapters.orders

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
import com.bumptech.glide.Glide
import com.example.courier.R
import com.example.courier.databinding.DeliveryTypeItemBinding
import com.example.courier.model.order.ListsResponse
import com.example.courier.views.orders.CreateOrderActivty

class DeliveryTypesAdapter(
    context : CreateOrderActivty,
    addressList : ArrayList<ListsResponse.DeliveryOptionData>,
    var activity : Context
) :
    RecyclerView.Adapter<DeliveryTypesAdapter.ViewHolder>() {
    private val mContext : CreateOrderActivty
    private var viewHolder : ViewHolder? = null
    private var bannersList : ArrayList<ListsResponse.DeliveryOptionData>?

    init {
        this.mContext = context
        this.bannersList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.delivery_type_item,
            parent,
            false
        ) as DeliveryTypeItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, bannersList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        val dis = (position + 1) * 10
        holder.binding!!.txtDeliveryType.text = bannersList!![position].title

        if (!TextUtils.isEmpty(bannersList!![position].selected) && bannersList!![position].selected.equals(
                "true"
            )
        ) {
            holder.binding.txtDeliveryType.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        } else {
            holder.binding.txtDeliveryType.setTextColor(mContext.resources.getColor(R.color.colorBlack))
        }


        holder.binding!!.txtDeliveryType.setOnClickListener {
            if (bannersList!![position].selected.equals("true")) {
                mContext.selectedDeliveryType(position, "false")
            } else {
                mContext.selectedDeliveryType(position, "true")
            }
        }
    }

    override fun getItemCount() : Int {
        return bannersList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : DeliveryTypeItemBinding?,
        mContext : CreateOrderActivty,
        addressList : ArrayList<ListsResponse.DeliveryOptionData>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}