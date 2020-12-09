package com.courierdriver.views.notification

import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.databinding.ActivityChatBinding
import com.courierdriver.model.NotificationListModel
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.NotificationViewModel

class ChatActivity : BaseActivity() {
    private var binding: ActivityChatBinding? = null
    private var viewModel: NotificationViewModel? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityChatBinding
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        binding!!.viewModel = viewModel

        val model = intent.getSerializableExtra("model") as NotificationListModel.Body?
        binding!!.model = model

        setToolbar()
    }

    private fun setToolbar() {
        binding!!.toolbarCommon.toolbar.setImageDrawable(getDrawable(R.drawable.ic_back_white))
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.notification_chat)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }
}