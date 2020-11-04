package com.android.courier.adapters.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.databinding.ContactItemBinding
import com.android.courier.views.contacts.ContactListActivity
import com.github.tamir7.contacts.Contact

class ContactListAdapter(
    context : ContactListActivity,
    addressList : MutableList<Contact>?,
    var activity : Context
) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    private val mContext : ContactListActivity
    private var viewHolder : ViewHolder? = null
    private var orderList : MutableList<Contact>?

    init {
        this.mContext = context
        this.orderList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.contact_item,
            parent,
            false
        ) as ContactItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, orderList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.txtContactName.text = orderList!![position].displayName
        if (orderList!![position].phoneNumbers.size > 0) {
            holder.binding!!.txtNumber.text = orderList!![position].phoneNumbers[0].number
        }

        holder.binding!!.topLay.setOnClickListener {
            // mContext.showToastSuccess("Clicked")
            mContext.selectContact(position)
        }
        holder.binding!!.txtContactName.setOnClickListener {
            //  mContext.showToastSuccess("Clicked")
            mContext.selectContact(position)
        }
        holder.binding!!.txtNumber.setOnClickListener {
            //  mContext.showToastSuccess("Clicked")
            mContext.selectContact(position)
        }

    }

    override fun getItemCount() : Int {
        return orderList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : ContactItemBinding?,
        mContext : ContactListActivity,
        addressList : MutableList<Contact>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}