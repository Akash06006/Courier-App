package com.courierdriver.views.notification

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.adapters.notification.NotificationChatAdapter
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.databinding.ActivityNotificationChatBinding
import com.courierdriver.model.NotificationListModel
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.NotificationViewModel

class NotificationChatActivity : BaseActivity() {
    private var binding: ActivityNotificationChatBinding? = null
    private var viewModel: NotificationViewModel? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityNotificationChatBinding
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        binding!!.viewModel = viewModel

        binding!!.toolbarCommon.toolbar.setImageDrawable(getDrawable(R.drawable.ic_back_white))
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.notification_chat)

        loaderObserver()
        getNotificationListObserver()
    }

    private fun getNotificationListObserver() {
        viewModel!!.notification()
        viewModel!!.notificationData().observe(this,
            Observer<NotificationListModel> { response ->
                stopProgressDialog()
                binding!!.rvItems.visibility = View.VISIBLE
                binding!!.tvNoRecord.visibility = View.GONE
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            setAdapter(response.body)
                        }
                        204 -> {
                            binding!!.tvNoRecord.visibility = View.VISIBLE
                            binding!!.rvItems.visibility = View.GONE
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setAdapter(notificationList: List<NotificationListModel.Body>?) {
        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = NotificationChatAdapter(this, notificationList)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding!!.rvItems.layoutManager = linearLayoutManager
        binding!!.rvItems.adapter = adapter
    }

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_notification_chat
    }
}