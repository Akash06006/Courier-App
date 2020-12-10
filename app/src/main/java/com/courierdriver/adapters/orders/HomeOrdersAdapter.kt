package com.courierdriver.adapters.orders

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.RowOrderListBinding
import com.courierdriver.model.order.OrderListModel
import com.courierdriver.views.home.fragments.HomeFragment
import com.courierdriver.views.orders.OrderDetailsActivity
import java.text.DecimalFormat

class HomeOrdersAdapter(
    var mContext: HomeFragment,
    var orderList: ArrayList<OrderListModel.Body>?,
    var orderStatus: Int
) : RecyclerView.Adapter<HomeOrdersAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_order_list,
            parent,
            false
        ) as RowOrderListBinding
        return ViewHolder(binding.root, viewType, binding, mContext, orderList, orderStatus)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.model = orderList!![position]
        holder.binding.currencySign = GlobalConstants.CURRENCY_SIGN

        orderList!![position].deliveryAddress?.let {
            val deliveryAddresses = orderList!![position].deliveryAddress
            holder.binding.deliveryAddress = deliveryAddresses!![deliveryAddresses.size - 1].address
        }

        holder.binding.tvPrice.text =
            GlobalConstants.CURRENCY_SIGN + DecimalFormat("##.##").format(orderList!![position].totalOrderPrice!!.toDouble())

        if (orderList!![position].deliveryoption!!.title == "Regular") {
            holder.binding.tvDeliveryOption.setTextColor(
                ContextCompat.getColor(
                    mContext.baseActivity,
                    R.color.colorPrimary
                )
            )
        } else if (orderList!![position].deliveryoption!!.title == "Express") {
            holder.binding.tvDeliveryOption.setTextColor(
                ContextCompat.getColor(
                    mContext.baseActivity,
                    R.color.colorRed
                )
            )
        }

        if (orderList!![position].deliveryAddress!!.size > 1)
            holder.binding.tvDeliveryAddressSize.visibility = View.VISIBLE
        else
            holder.binding.tvDeliveryAddressSize.visibility = View.GONE
        setOrderButtons(holder.binding)
        //  setAdapter(holder.binding, orderList!![position].deliveryAddress)
    }

    private fun setOrderButtons(binding: RowOrderListBinding) {
        when (orderStatus) {
            1 -> {
                /*  binding.tvTakeOrder.text = mContext.getString(R.string.accept)
                  binding.linActiveOrder.visibility = View.VISIBLE*/
                binding.relRoute.visibility = View.VISIBLE
                binding.tvDate.visibility = View.VISIBLE
                //   binding.viewRoute.visibility = View.VISIBLE
            }
            2 -> {
                /* binding.tvTakeOrder.text = mContext.getString(R.string.take_order)
                 binding.linActiveOrder.visibility = View.VISIBLE*/
                binding.relRoute.visibility = View.VISIBLE
                binding.tvDate.visibility = View.VISIBLE
                // binding.viewRoute.visibility = View.VISIBLE
            }
            3 -> {
//                binding.linActiveOrder.visibility = View.GONE
                binding.relRoute.visibility = View.GONE
                binding.viewRoute.visibility = View.GONE
                binding.viewItems.visibility = View.GONE
                binding.tvDate.visibility = View.GONE
            }
        }
    }

/*
    private fun setAdapter(
        binding: RowOrderListBinding,
        deliveryAddressList: ArrayList<OrderListModel.Body.DeliveryAddress>?
    ) {
        val linearLayoutManager = LinearLayoutManager(mContext.baseActivity)
        val deliveryAddressAdapter =
            DeliveryAddressAdapter(mContext, deliveryAddressList!!, orderStatus)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvDeliveryAddress.layoutManager = linearLayoutManager
        binding.rvDeliveryAddress.adapter = deliveryAddressAdapter
    }
*/

    override fun getItemCount(): Int {
        return orderList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowOrderListBinding?,
        mContext: HomeFragment,
        orderList: ArrayList<OrderListModel.Body>?,
        var orderStatus: Int
    ) : RecyclerView.ViewHolder(v) {
        init {

            /* binding!!.tvTakeOrder.setOnClickListener {
                 mContext.acceptOrder(orderList!![adapterPosition].id, adapterPosition)
             }
             binding.tvCancelOrder.setOnClickListener {
                 mContext.cancelOrder(orderList!![adapterPosition].id, adapterPosition)
             }*/
            binding!!.cvOrderList.setOnClickListener {
                val intent = Intent(mContext.activity, OrderDetailsActivity::class.java)
                intent.putExtra("id", orderList!![adapterPosition].id)
                intent.putExtra("active", "true")
                intent.putExtra(
                    "orderStatus",
                    orderStatus.toString()
                ) // 1- Available, 2 - Active, 3 - Completed
                mContext.startActivity(intent)
            }
        }
    }
}
