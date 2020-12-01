package com.android.courier.adapters.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.courier.R
import com.android.courier.databinding.DiscountItemBinding
import com.android.courier.model.order.ListsResponse
import com.android.courier.views.home.fragments.HomeFragment
import com.android.courier.views.orders.CreateOrderActivty
import com.payumoney.sdkui.ui.activities.BaseActivity

class DiscountListAdapter(
    context : HomeFragment,
    addressList : ArrayList<ListsResponse.BannersData>
) :
    RecyclerView.Adapter<DiscountListAdapter.ViewHolder>() {
    private val mContext : HomeFragment
    private var viewHolder : ViewHolder? = null
    private var bannersList : ArrayList<ListsResponse.BannersData>?

    init {
        this.mContext = context
        this.bannersList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.discount_item,
            parent,
            false
        ) as DiscountItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, bannersList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        /* Glide.with(mContext).load(bannersList!![position].icon).placeholder(R.drawable.ic_dummy)
             .into(holder.binding!!.imgBanner)

         holder.binding!!.imgBanner.setOnClickListener {
             if (bannersList!![position].type.equals("coupon")) {
                 mContext.showOfferInformation(position)
             } else {
                 mContext.baseActivity.showToastSuccess("No Information")
             }
         }*/
    }

    override fun getItemCount() : Int {
        return 6//bannersList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : DiscountItemBinding?,
        mContext : HomeFragment,
        addressList : ArrayList<ListsResponse.BannersData>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}