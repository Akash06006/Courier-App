package com.example.courier.views.webviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.lifecycle.ViewModelProviders
import com.example.courier.databinding.ActivityWebViewBinding
import android.webkit.WebViewClient
import com.example.courier.R
import com.example.courier.constants.GlobalConstants
import com.example.courier.utils.BaseActivity
import com.example.courier.viewmodels.MyAccountsViewModel

class WebViewActivity : BaseActivity() {
    lateinit var binding : ActivityWebViewBinding
    private var accountsViewModel : MyAccountsViewModel? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityWebViewBinding
        accountsViewModel = ViewModelProviders.of(this).get(MyAccountsViewModel::class.java)
        binding.myaccountsViewModel = accountsViewModel
        //binding.webView.webViewClient = MyWebViewClient(this)
        //binding.webView.loadUrl("https://www.javatpoint.com/")
        val title = intent.extras.get("title").toString()
        binding.commonToolBar.imgToolbarText.setText(title)
        binding.commonToolBar.imgRight.visibility = View.GONE
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view : WebView?, url : String?) : Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        if (title.equals(resources.getString(R.string.terms_and_conditions))) {
            binding.webView.loadUrl(GlobalConstants.TERMS_CONDITION)
        } else if (title.equals(resources.getString(R.string.privacy_policy))) {
            binding.webView.loadUrl(GlobalConstants.PRIVACY_POLICY)
        } else {
            //  binding.webView.loadUrl(GlobalConstants.ABOUT_US)
        }
        //  binding.webView.loadUrl("https://www.cerebruminfotech.com/")

    }

    override fun getLayoutId() : Int {
        return R.layout.activity_web_view
    }

}
