package com.android.courier.views.payments

import android.app.Dialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.payments.TransactionsListAdapter
import com.android.courier.common.UtilsFunctions
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogClass
import com.google.gson.JsonObject
import com.android.courier.databinding.ActivityTransactionBinding
import com.android.courier.model.payments.TransactionResponse
import com.android.courier.model.payments.Transactions
import com.android.courier.viewmodels.payments.PaymentsViewModel

class TransactionHistoryActivity : BaseActivity() {
    lateinit var loyaltyBinding : ActivityTransactionBinding
    lateinit var paymentsViewModel : PaymentsViewModel
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    var transactionList = ArrayList<Transactions>()
    override fun getLayoutId() : Int {
        return R.layout.activity_transaction
    }

    override fun onBackPressed() {
        finish()
    }

    override fun initViews() {
        loyaltyBinding = viewDataBinding as ActivityTransactionBinding
        paymentsViewModel = ViewModelProviders.of(this).get(PaymentsViewModel::class.java)
        loyaltyBinding.commonToolBar.imgRight.visibility = View.GONE
        loyaltyBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.transaction_history)
        //  loyaltyBinding.loyaltyViewModel = paymentsViewModel
        //UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        if (UtilsFunctions.isNetworkConnectedReturn()) {
           startProgressDialog()
        }

        paymentsViewModel.getTransactionList().observe(this,
            Observer<TransactionResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            transactionList.clear()

                            transactionList.addAll(response.data?.transactions!!)
                            if (transactionList.size > 0) {
                                initTranctionsAdapter()
                                loyaltyBinding.rvTransaction.visibility = View.VISIBLE
                                loyaltyBinding.txtNoRecord.visibility = View.GONE
                            } else {
                                loyaltyBinding.rvTransaction.visibility = View.GONE
                                loyaltyBinding.txtNoRecord.visibility = View.VISIBLE
                            }

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            loyaltyBinding.txtNoRecord.visibility = View.VISIBLE
                            // loyaltyBinding.llPoints.visibility = View.GONE
                        }
                    }
                }
            })
        paymentsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgBack" -> {
                        onBackPressed()
                    }
                }
            })
        )

    }

    private fun initTranctionsAdapter() {
        val ordersAdapter =
            TransactionsListAdapter(
                this@TransactionHistoryActivity,
                transactionList,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        loyaltyBinding.rvTransaction.layoutManager = linearLayoutManager
        loyaltyBinding.rvTransaction.setHasFixedSize(true)
        loyaltyBinding.rvTransaction.adapter = ordersAdapter
        loyaltyBinding.rvTransaction.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

}
