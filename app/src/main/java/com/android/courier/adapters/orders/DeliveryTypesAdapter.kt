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
import com.android.courier.databinding.DeliveryTypeItemBinding
import com.android.courier.model.order.ListsResponse
import com.android.courier.views.orders.CreateOrderActivty
import com.android.courier.views.orders.fragments.CreateOrderFirstFragment

class DeliveryTypesAdapter(
    context : CreateOrderFirstFragment,
    addressList : ArrayList<ListsResponse.DeliveryOptionData>
) :
    RecyclerView.Adapter<DeliveryTypesAdapter.ViewHolder>() {
    private val mContext : CreateOrderFirstFragment
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
        holder.binding!!.txtDelType.text = bannersList!![position].title

        if (!TextUtils.isEmpty(bannersList!![position].selected) && bannersList!![position].selected.equals(
                "true"
            )
        ) {
            holder.binding.txtDelType.setTextColor(mContext.resources.getColor(R.color.colorPrimary))

            holder.binding!!.txtDelType.setBackground(mContext.resources.getDrawable(R.drawable.ic_del_type_selected))
            /*  holder.binding!!.toLayout.setBackgroundTintList(
                  mContext.getResources().getColorStateList(R.color.colorPrimary)
              )*/
            // holder.binding.txtName.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        } else {
//            holder.binding.txtDeliveryType.setTextColor(mContext.resources.getColor(R.color.colorBlack))
            holder.binding.txtDelType.setTextColor(mContext.resources.getColor(R.color.colorBlack))

            holder.binding!!.txtDelType.setBackground(mContext.resources.getDrawable(R.drawable.ic_del_type_unselected))
        }


        holder.binding!!.txtDelType.setOnClickListener {
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
        mContext : CreateOrderFirstFragment,
        addressList : ArrayList<ListsResponse.DeliveryOptionData>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}