package com.courierdriver.adapters.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.RowMonthlyStatisticsBinding
import com.courierdriver.model.StatisticsModel
import com.courierdriver.utils.BaseActivity

class StatisticsAdapter(
    var mContext: BaseActivity,
    var statisticsList: ArrayList<StatisticsModel.Body.Year>?,
    var monthName: String
) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    private var viewHolder: ViewHolder? = null

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_monthly_statistics,
            parent,
            false
        ) as RowMonthlyStatisticsBinding
        return ViewHolder(binding.root, viewType, binding)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.currencySign = GlobalConstants.CURRENCY_SIGN
        holder.binding.model = statisticsList!![position]

        if(monthName!="All") {
            if (monthName == statisticsList!![position].month)
                holder.binding.linMain.visibility = View.VISIBLE
            else
                holder.binding.linMain.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return statisticsList!!.size
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowMonthlyStatisticsBinding?
    ) : RecyclerView.ViewHolder(v) {
        init {
        }
    }
}
