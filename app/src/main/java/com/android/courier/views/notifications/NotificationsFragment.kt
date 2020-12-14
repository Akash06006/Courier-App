package com.android.courier.views.notifications

import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.bumptech.glide.Glide
import com.android.courier.databinding.FragmentNotificationBinding
import com.android.courier.adapters.notifications.NotificationListAdapter
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.model.notifications.Data
import com.android.courier.model.notifications.NotificationsResponse
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseFragment
import com.android.courier.viewmodels.notifications.NotificaionViewModel
import com.android.courier.views.home.LandingActivty

class
NotificationsFragment : BaseFragment() {
    private lateinit var notificaionViewModel : NotificaionViewModel
    var notificationList = ArrayList<Data>()
    private lateinit var fragmentNotificationsBinding : FragmentNotificationBinding
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_notification
    }

    override fun onResume() {
        super.onResume()

    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentNotificationsBinding = viewDataBinding as FragmentNotificationBinding
        notificaionViewModel = ViewModelProviders.of(this).get(NotificaionViewModel::class.java)
        fragmentNotificationsBinding.orderViewModel = notificaionViewModel

        if (UtilsFunctions.isNetworkConnected()) {
            baseActivity.startProgressDialog()
            //orderViewModel("active")
        }
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()
        Glide.with(activity!!).load(userImage).placeholder(R.drawable.ic_user)
            .into(fragmentNotificationsBinding.imgRight)
        notificaionViewModel.getList().observe(this,
            Observer<NotificationsResponse> { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            notificationList.clear()
                            notificationList.addAll(response.data!!)
                            if (notificationList.size > 0) {
                                initNotificationsAdapter()
                                fragmentNotificationsBinding.txtNoRecord.visibility = View.GONE
                                fragmentNotificationsBinding.noRecordAnimation.visibility =
                                    View.GONE
                            } else {
                                fragmentNotificationsBinding.txtNoRecord.visibility = View.VISIBLE
                                fragmentNotificationsBinding.noRecordAnimation.visibility =
                                    View.VISIBLE
                            }

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            fragmentNotificationsBinding.txtNoRecord.visibility = View.VISIBLE
                            fragmentNotificationsBinding.noRecordAnimation.visibility = View.VISIBLE
                        }
                    }
                }
            })


        fragmentNotificationsBinding.toolbar.setImageResource(R.drawable.ic_side_menu)

        Glide.with(activity!!).load(userImage).placeholder(R.drawable.ic_user)
            .into(fragmentNotificationsBinding.toolbarCommon.imgRight)
        notificaionViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "toolbar" -> {
                        (activity as LandingActivty).openCloseDrawer()
                    }
                }
            })
        )

    }

    private fun initNotificationsAdapter() {
        val ordersAdapter =
            NotificationListAdapter(
                this@NotificationsFragment,
                notificationList,
                activity!!
            )
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_left)
        fragmentNotificationsBinding.rvOrders.setLayoutAnimation(controller);
        fragmentNotificationsBinding.rvOrders.scheduleLayoutAnimation();
        val linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentNotificationsBinding.rvOrders.layoutManager = linearLayoutManager
        fragmentNotificationsBinding.rvOrders.setHasFixedSize(true)
        fragmentNotificationsBinding.rvOrders.adapter = ordersAdapter
        fragmentNotificationsBinding.rvOrders.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

}