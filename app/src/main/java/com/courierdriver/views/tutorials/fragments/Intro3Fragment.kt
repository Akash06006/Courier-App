package com.courierdriver.views.tutorials.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.courierdriver.R
import com.courierdriver.views.home.LandingActivty
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_intro3, container, false)

        view.btnStart.setOnClickListener {
            startActivity(Intent(activity, LandingActivty::class.java))
            activity!!.finish()
        }

        return view
    }
}
