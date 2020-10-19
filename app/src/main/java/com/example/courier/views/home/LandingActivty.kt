package com.example.courier.views.home

import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.courier.R
import com.example.courier.application.MyApplication
import com.example.courier.constants.GlobalConstants
import com.example.courier.databinding.ActivityLandingActivtyBinding
import com.example.courier.model.CommonModel
import com.example.courier.sharedpreference.SharedPrefClass
import com.example.courier.utils.BaseActivity
import com.example.courier.utils.DialogClass
import com.example.courier.utils.DialogssInterface
import com.example.courier.viewmodels.home.DashboardViewModel
import com.example.courier.views.authentication.LoginActivity
import com.example.courier.views.chat.ChatActivity
import com.example.courier.views.home.fragments.HomeFragment
import com.example.courier.views.notifications.NotificationsFragment
import com.example.courier.views.orders.CreateOrderActivty
import com.example.courier.views.orders.fragments.OrdersFragment
import com.example.courier.views.payments.TransactionHistoryActivity
import com.example.courier.views.profile.ProfileFragment
import com.example.courier.views.promocode.PromoCodeActivity
import com.example.courier.views.refer.LoyaltyPointsListActivity
import com.example.courier.views.refer.ReferAndEarn
import com.example.courier.views.tutorials.TutorialActivity
import com.example.courier.views.webviews.WebViewActivity
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class LandingActivty : BaseActivity(), DialogssInterface {
    private lateinit var landingBinding : ActivityLandingActivtyBinding
    private var dashboardViewModel : DashboardViewModel? = null
    private var navigationView : NavigationView? = null
    private var drawer : DrawerLayout? = null
    private var confirmationDialog : Dialog? = null
    private var ratingDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    var pos = 0
    override fun getLayoutId() : Int {
        return R.layout.activity_landing_activty
    }

    override fun onBackPressed() {
        if (pos == 0) {
            finish()
        } else {
            val fragment = HomeFragment()
            this.callFragments(
                fragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
            landingBinding!!.tablayout.getTabAt(0)?.select()

        }

    }

    override fun initViews() {
        landingBinding = viewDataBinding as ActivityLandingActivtyBinding
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        landingBinding!!.dashboardViewModel = dashboardViewModel
        navigationView = landingBinding!!.navView
        // navigationView!!.alpha = 0.9f
        drawer = landingBinding!!.drawerLayout
        // ic_profile
        val userId = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USERID
        )
        val image = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USER_IMAGE
        )
        Glide.with(this)
            .load(image)
            .placeholder(R.drawable.ic_user)
            .into(landingBinding!!.icProfile)
        val name = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USERNAME
        )
        val email = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USEREMAIL
        )

        GlobalConstants.PRIVACY_POLICY = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.PRIVACY_POLICY
        ).toString()
        GlobalConstants.TERMS_CONDITION = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.TERMS_CONDITION
        ).toString()
        //val contacts : List<Contact> = Contacts.getQuery().find()
        landingBinding.tvName.text = name.toString()
        landingBinding.tvEmail.text = email.toString()
        val fragment = HomeFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")
        landingBinding!!.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab?) {
                landingBinding!!.tablayout!!.getTabAt(0)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_tab_home));
                landingBinding!!.tablayout!!.getTabAt(1)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_tab_order));
                landingBinding!!.tablayout!!.getTabAt(2)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_tab_create));
                landingBinding!!.tablayout!!.getTabAt(3)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_tab_notification));
                landingBinding!!.tablayout!!.getTabAt(4)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_tab_user));
                var fragment : Fragment? = null
                fragment = HomeFragment()
                //   activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE
                when (tab!!.position) {
                    0 -> {
                        /* activityOtpVerificationBinding!!.toolbarCommon.imgToolbarText.setText(
                             resources.getString(
                                 R.string.home
                             )
                         )*/
                        landingBinding!!.tablayout.getTabAt(0)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_tab_home_selected));
                        pos = 0
                        fragment = HomeFragment()
                    }
                    1 -> {
                        /* activityOtpVerificationBinding!!.toolbarCommon.imgToolbarText.setText(
                             resources.getString(
                                 R.string.my_booking
                             )
                         )*/
                        landingBinding!!.tablayout.getTabAt(1)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_tab_order_selected));
                        pos = 1
                        fragment = OrdersFragment()
                    }
                    2 -> {
                        val intent = Intent(this@LandingActivty, CreateOrderActivty::class.java)
                        startActivity(intent)
                        landingBinding!!.tablayout.getTabAt(0)?.select()
                    }
                    3 -> {
                        landingBinding!!.tablayout.getTabAt(3)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_tab_notification_selected));
                        pos = 2
                        //showToastSuccess("Coming Soon")
                        fragment = NotificationsFragment()
                    }
                    4 -> {
                        landingBinding!!.tablayout.getTabAt(4)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_tab_user_selected));
                        pos = 3
                        /*  activityOtpVerificationBinding!!.toolbarCommon.imgToolbarText.setText(
                              resources.getString(
                                  R.string.profile
                              )
                          )*/
                        fragment = ProfileFragment()
                    }
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")
                /* Handler().postDelayed({
                     setHeadings()
                 }, 300)*/

            }

            override fun onTabUnselected(tab : TabLayout.Tab?) {

            }

            override fun onTabReselected(tab : TabLayout.Tab?) {
                //var fragment : Fragment? = null
                //Not In use
            }
        })


        dashboardViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                drawer!!.closeDrawer(Gravity.LEFT)
                when (it) {
                    "tv_nav_my_account" -> {
                        val fragment = ProfileFragment()
                        landingBinding!!.drawerLayout.closeDrawers()
                        this.callFragments(
                            fragment,
                            supportFragmentManager,
                            false,
                            "send_data",
                            ""
                        )
                        landingBinding!!.tablayout.getTabAt(4)?.select()
                        landingBinding!!.drawerLayout.closeDrawers()
                    }
                    "tv_nav_refer_earn" -> {
                        val intent = Intent(this, ReferAndEarn::class.java)
                        startActivity(intent)
                        // showToastSuccess("Coming Soon")
                    }
                    "tv_nav_loyalty_points" -> {
                        val intent = Intent(this, LoyaltyPointsListActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_offer_zone" -> {
                        val intent = Intent(this, PromoCodeActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_payment" -> {
                        val intent = Intent(this, TransactionHistoryActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_help" -> {
                        val intent = Intent(this, TutorialActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_privacy_policy" -> {
                        /* val intent1 = Intent(this, WebViewActivity::class.java)
                         intent1.putExtra("title", getString(R.string.privacy_policy))
                         startActivity(intent1)*/
                        showToastSuccess("Coming Soon")
                    }
                    "tv_nav_terms_condition" -> {
                        /*  val intent1 = Intent(this, WebViewActivity::class.java)
                          intent1.putExtra("title", getString(R.string.terms_and_conditions))
                          startActivity(intent1)*/
                        showToastSuccess("Coming Soon")
                    }
                    "tv_nav_home" -> {
                        val fragment = HomeFragment()
                        // activityDashboardBinding!!.toolbarCommon.imgToolbarText.setText("")
                        /*   activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                               getString(R.string.home)*/
                        landingBinding!!.drawerLayout.closeDrawers()
                        this.callFragments(
                            fragment,
                            supportFragmentManager,
                            false,
                            "send_data",
                            ""
                        )
                        landingBinding!!.tablayout.getTabAt(0)?.select()
                        landingBinding!!.drawerLayout.closeDrawers()

                    }
                    "tv_nav_support" -> {
                        /* val intent = Intent(this, ChatActivity::class.java)
                         startActivity(intent)*/
                        showToastSuccess("Coming Soon")
                    }
                    "tv_nav_more" -> {
                        // val intent = Intent(this, OrdersHistoryListActivity::class.java)
                        // startActivity(intent)
                        showToastSuccess("Coming Soon")
                    }
                    "tv_nav_terms" -> {
                        showToastSuccess("Coming Soon")
                        /* val intent = Intent(this, OrdersHistoryListActivity::class.java)
                         startActivity(intent)*/
                    }
                    "ic_profile" -> {
                        //val intent = Intent(this, ProfileActivity::class.java)
                        //startActivity(intent)
                    }
                    "tv_nav_setting" -> {
                        //val intent = Intent(this, MyAccountsActivity::class.java)
                        // startActivity(intent)
                    }
                    "img_nav_cancel" -> {
                        landingBinding!!.drawerLayout.closeDrawers()
                    }
                    "tv_nav_chat" -> {
                        //  val intent = Intent(this, ChatActivity::class.java)
                        // startActivity(intent)
                    }
                    "tv_nav_logout" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            this,
                            this,
                            "logout",
                            "Do you really want to logout?"
                        )
                        confirmationDialog?.show()

                    }
                    "toolbar" -> {
                        val image = SharedPrefClass().getPrefValue(
                            MyApplication.instance.applicationContext,
                            GlobalConstants.USER_IMAGE
                        )
                        // ic_profile
                        Glide.with(this)
                            .load(image)
                            .placeholder(R.drawable.ic_person)
                            .into(landingBinding!!.icProfile)

                        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
                            drawer!!.closeDrawer(Gravity.LEFT) //CLOSE Nav Drawer!
                        } else {
                            drawer!!.openDrawer(Gravity.LEFT)
                        }
                        val fragmentType =
                            supportFragmentManager.findFragmentById(R.id.frame_layout)
                        /*when (fragmentType) {
                            is HomeFragment -> {
                                activityDashboardBinding!!.toolbarCommon.imgRight.visibility =
                                    View.GONE
                            }
                        }*/
                    }
                }
            })
        )

        dashboardViewModel!!.getLogoutReposne.observe(this,
            Observer<CommonModel> { logoutResponse->
                this.stopProgressDialog()

                if (logoutResponse != null) {
                    val message = logoutResponse.message

                    if (logoutResponse.code == 200) {
                        SharedPrefClass().putObject(
                            this,
                            "isLogin",
                            false
                        )

                        SharedPrefClass().putObject(
                            this,
                            GlobalConstants.USER_IMAGE,
                            "null"
                        )
                        showToastSuccess(getString(R.string.logout_msg))
                        val intent1 = Intent(this, LoginActivity::class.java)
                        startActivity(intent1)
                        finish()

                    } else {
                        showToastError(message)
                    }
                }
            })

    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "logout" -> {
                confirmationDialog?.dismiss()
                dashboardViewModel!!.callLogoutApi()
                // dashboardViewModel!!.callLogoutApi()

            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String?) {
        when (mKey) {
            "logout" -> confirmationDialog?.dismiss()
        }
    }

    public fun openCloseDrawer() {
        val image = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USER_IMAGE
        )
        Glide.with(this)
            .load(image)
            .placeholder(R.drawable.ic_user)
            .into(landingBinding!!.icProfile)
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(Gravity.LEFT) //CLOSE Nav Drawer!
        } else {
            drawer!!.openDrawer(Gravity.LEFT)
        }
    }

}
