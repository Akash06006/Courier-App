package com.courierdriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.databinding.RowPaymentHistoryBinding
import com.courierdriver.model.PaymentHistoryModel
import com.courierdriver.utils.BaseActivity

class PaymentHistoryAdapter(
    var mContext: BaseActivity,
    var paymentsList: ArrayList<PaymentHistoryModel.Body>?
) : RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_payment_history,
            parent,
            false
        ) as RowPaymentHistoryBinding
        return ViewHolder(binding.root, viewType, binding)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.model = paymentsList!![position]
    }

    override fun getItemCount(): Int {
        return paymentsList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowPaymentHistoryBinding?
    ) : RecyclerView.ViewHolder(v) {
        init {
        }
    }
}
