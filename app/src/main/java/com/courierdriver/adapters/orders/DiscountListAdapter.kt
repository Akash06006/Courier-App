package com.courierdriver.adapters.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.databinding.DiscountItemBinding
import com.courierdriver.model.DiscountModel
import com.courierdriver.views.orders.CreateOrderActivty

class DiscountListAdapter(
    context: CreateOrderActivty,
    addressList: ArrayList<DiscountModel>?,
    var activity: Context
) :
    RecyclerView.Adapter<DiscountListAdapter.ViewHolder>() {
    private val mContext: CreateOrderActivty
    private var viewHolder: ViewHolder? = null
    private var tipList: ArrayList<DiscountModel>?

    init {
        this.mContext = context
        this.tipList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.discount_item,
            parent,
            false
        ) as DiscountItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, tipList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        val dis = (position + 1) * 10
        holder.binding!!.txtTip.text = dis.toString() + "%Off"/*dateList[position].date*/
        /* if (tipList[position].selected.equals("true")) {
             holder.binding!!.txtTip.setBackground(mContext.resources.getDrawable(R.drawable.ic_tip_selected_back))
             holder.binding.txtTip.setTextColor(mContext.resources.getColor(R.color.colorWhite))
         } else {
             // holder.binding.topLay.setBackgroundResource(R.drawable.shape_round_corner)
             holder.binding.txtTip.setTextColor(mContext.resources.getColor(R.color.colorBlack))
             holder.binding!!.txtTip.setBackground(mContext.resources.getDrawable(R.drawable.ic_tip_back))
         }*/
        /* holder.binding!!.txtTip.setOnClickListener {
             if (tipList[position].selected.equals("true")) {
                 //  mContext.selectTip(position, "false")
             } else {
                 //  mContext.selectTip(position, "true")
             }
         }*/
    }

    override fun getItemCount(): Int {
        return 4//dateList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: DiscountItemBinding?,
        mContext: CreateOrderActivty,
        addressList: ArrayList<DiscountModel>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}