package com.android.courier.views.contacts

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.contacts.ContactListAdapter
import com.android.courier.databinding.ActivityContactBinding
import com.android.courier.utils.BaseActivity
import com.android.courier.viewmodels.payments.PaymentsViewModel
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts

class ContactListActivity : BaseActivity() {
    private var contacts : MutableList<Contact>? = null
    lateinit var contactBinding : ActivityContactBinding
    lateinit var paymentsViewModel : PaymentsViewModel
    var contactListAdapter : ContactListAdapter? = null
    override fun getLayoutId() : Int {
        return R.layout.activity_contact
    }

    override fun onBackPressed() {
        finish()
    }

    override fun initViews() {
        contactBinding = viewDataBinding as ActivityContactBinding
        paymentsViewModel = ViewModelProviders.of(this).get(PaymentsViewModel::class.java)
        contactBinding.commonToolBar.imgRight.visibility = View.GONE
        contactBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.contacts)
        //  loyaltyBinding.loyaltyViewModel = paymentsViewModel
        //UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        contactBinding.edtSearch.addTextChangedListener(textWatcher);
        contacts = Contacts.getQuery().find()

        if (contacts?.size!! > 0)
            initContactAdapter()
        paymentsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgBack" -> {
                        onBackPressed()
                    }
                }
            })
        )

    }

    var textWatcher : TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s : CharSequence,
            start : Int,
            count : Int,
            after : Int
        ) {
        }

        override fun onTextChanged(
            s : CharSequence,
            start : Int,
            before : Int,
            count : Int
        ) {
            if (!TextUtils.isEmpty(s.toString())) {
                /* val mainQuery =
                     Contacts.getQuery()
                 val q : Query = Contacts.getQuery()
                 q.whereContains(Contact.Field.DisplayName, s.toString())
                 val q2 =
                     Contacts.getQuery()
                 q2.whereStartsWith(Contact.Field.PhoneNumber, s.toString())
                 val qs : MutableList<Query> =
                     ArrayList()
                 qs.add(q)
                 qs.add(q2)
                 mainQuery.or(qs)*/
                contacts?.clear()
                contactListAdapter?.notifyDataSetChanged()
                val q =
                    Contacts.getQuery()
                q.whereContains(Contact.Field.DisplayName, s.toString())
                contacts = q.find()
                initContactAdapter()
                //contacts = mainQuery.find()
                //contactListAdapter?.notifyDataSetChanged()
                // contacts = Contacts.getQuery().find()
            } else {
                contacts = Contacts.getQuery().find()
                initContactAdapter()
                // contactListAdapter?.notifyDataSetChanged()
            }

        }

        override fun afterTextChanged(s : Editable) {}
    }

    private fun initContactAdapter() {
        contactListAdapter =
            ContactListAdapter(
                this@ContactListActivity,
                contacts,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        contactBinding.rvContacts.layoutManager = linearLayoutManager
        contactBinding.rvContacts.setHasFixedSize(true)
        contactBinding.rvContacts.adapter = contactListAdapter
        contactBinding.rvContacts.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    fun selectContact(position : Int) {
        if (contacts!![position].phoneNumbers.size > 0) {
            val num = contacts!![position].phoneNumbers[0].number
            val intent = Intent();
            intent.putExtra("num", num);
            setResult(201, intent);
            finish()
        } else {
            showToastError("This is not a valid number")
        }

    }

}
