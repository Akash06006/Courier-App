package com.courierdriver.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.courierdriver.R

abstract class BaseFragment : Fragment() {
    lateinit var viewDataBinding: ViewDataBinding
    lateinit var baseActivity: BaseActivity
        private set

    protected abstract fun getLayoutResId(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity)
            baseActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        val viewRootBinding = viewDataBinding.root

        baseActivity.overridePendingTransition(
            R.anim.slide_in,
            R.anim.slide_out
        )
        return viewRootBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    protected abstract fun initView()

}