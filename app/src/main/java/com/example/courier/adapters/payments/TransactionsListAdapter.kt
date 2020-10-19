package com.example.courier.adapters.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.courier.R
import com.example.courier.databinding.TransactionItemBinding
import com.example.courier.model.payments.Transactions
import com.example.courier.views.payments.TransactionHistoryActivity

class TransactionsListAdapter(
    context : TransactionHistoryActivity,
    addressList : ArrayList<Transactions>,
    var activity : Context
) :
    RecyclerView.Adapter<TransactionsListAdapter.ViewHolder>() {
    private val mContext : TransactionHistoryActivity
    private var viewHolder : ViewHolder? = null
    private var orderList : ArrayList<Transactions>?

    init {
        this.mContext = context
        this.orderList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.transaction_item,
            parent,
            false
        ) as TransactionItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, orderList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.txtOrderName.text = orderList!![position].itemName

        holder.binding!!.txtOrderNumber.text = "#" + orderList!![position].orderNo
        holder.binding!!.txtPrice.text =
            orderList!![position].orderPrice + "₹"
        holder.binding!!.txtPayType.text =
            orderList!![position].paymentType
        holder.binding!!.txtDate.text =
            orderList!![position].createdAt

    }

    override fun getItemCount() : Int {
        return orderList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : TransactionItemBinding?,
        mContext : TransactionHistoryActivity,
        addressList : ArrayList<Transactions>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}