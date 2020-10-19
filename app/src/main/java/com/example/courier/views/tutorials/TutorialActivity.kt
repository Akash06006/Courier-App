package com.example.courier.views.tutorials

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.courier.R
import com.example.courier.application.MyApplication
import com.example.courier.constants.GlobalConstants
import com.example.courier.sharedpreference.SharedPrefClass
import com.example.courier.views.home.LandingActivty
import com.example.courier.views.tutorials.fragments.Intro1Fragment
import com.example.courier.views.tutorials.fragments.Intro2Fragment
import com.example.courier.views.tutorials.fragments.Intro3Fragment
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {
    private val fragmentList = ArrayList<Fragment>()
    var indicatorLayout : IndicatorLayout? = null
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        // making the status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_tutorial)
        //indicatorLayout = IndicatorLayout(this)
        val adapter = IntroSliderAdapter(this)
        vpIntroSlider.adapter = adapter
        SharedPrefClass().putObject(
            MyApplication.instance,
            GlobalConstants.TUTORIAL_VISIBLE,
            "true"
        )
        fragmentList.addAll(
            listOf(
                Intro1Fragment(), Intro2Fragment(), Intro3Fragment()
            )
        )
        adapter.setFragmentList(fragmentList)
        //indicatorLayout?.setIndicatorCount(adapter.itemCount)
        // indicatorLayout?.selectCurrentPosition(0)
        registerListeners()
    }

    private fun registerListeners() {
        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position : Int) {
                // indicatorLayout?.selectCurrentPosition(position)
                if (position < fragmentList.lastIndex) {
                    // tvSkip.visibility = View.VISIBLE
                    tvNext.text = "Next"
                } else {
                    tvSkip.visibility = View.GONE
                    tvNext.text = "Get Started"
                }
            }
        })
        tvSkip.setOnClickListener {
            startActivity(Intent(this, LandingActivty::class.java))
            finish()
        }
        tvNext.setOnClickListener {
            val position = vpIntroSlider.currentItem
            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            } else {
                startActivity(Intent(this, LandingActivty::class.java))
                finish()
            }
        }
    }
}