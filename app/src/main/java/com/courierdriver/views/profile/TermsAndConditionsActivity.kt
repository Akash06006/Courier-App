package com.courierdriver.views.profile

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.courierdriver.R
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityTermsConditionsBinding
import com.courierdriver.utils.BaseActivity

class TermsAndConditionsActivity : BaseActivity() {
    private var binding: ActivityTermsConditionsBinding? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityTermsConditionsBinding

        val url = intent.getStringExtra("url")
        binding!!.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding!!.webView.loadUrl(url)
        binding!!.webView.webViewClient = MyWebViewClient(binding!!)
    }

    private class MyWebViewClient(var binding: ActivityTermsConditionsBinding) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url!!)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            binding.progressBar.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            binding.progressBar.visibility = View.GONE
            super.onPageFinished(view, url)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_terms_conditions
    }
}