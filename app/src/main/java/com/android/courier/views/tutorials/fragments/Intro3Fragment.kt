package com.android.courier.views.tutorials.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.courier.R
import com.android.courier.application.MyApplication
import com.android.courier.constants.GlobalConstants
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.views.authentication.LoginActivity
import com.android.courier.views.home.LandingActivty
import kotlinx.android.synthetic.main.fragment_intro3.*
import kotlinx.android.synthetic.main.fragment_intro3.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Intro3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Intro3Fragment : Fragment() {
    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?
    ) : View? {
        val view : View = inflater.inflate(R.layout.fragment_intro3, container, false)
        val login = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            "isLogin"
        ).toString()
        view.btnStart.setOnClickListener {
            if (login == "true") {
                // startActivity(Intent(activity, LoginActivity::class.java))
                activity!!.finish()
            } else {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity!!.finish()
            }
        }

        return view
    }
}
