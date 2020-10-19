package com.example.courier.adapters

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.courier.R
import com.example.courier.databinding.PromoCodeItemBinding
import com.example.courier.utils.DialogssInterface
import com.example.courier.views.promocode.PromoCodeActivity
import com.example.services.model.promocode.PromoCodeListResponse
import java.util.*
import kotlin.collections.ArrayList

class PromoCodeListAdapter(
    context : PromoCodeActivity,
    addressList : ArrayList<PromoCodeListResponse.Body>,
    var activity : Context
) : DialogssInterface,
    RecyclerView.Adapter<PromoCodeListAdapter.ViewHolder>() {
    private val mContext : PromoCodeActivity
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<PromoCodeListResponse.Body>

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.promo_code_item,
            parent,
            false
        ) as PromoCodeItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.tvPromo.text = addressList[position].name
        holder.binding!!.tvPromoCode.text = addressList[position].code
        // holder.binding!!.tvDiscount.text = addressList[position].discount + "%"
        Glide.with(mContext)
            .load(addressList[position].icon)
            // .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_dummy)
            .into(holder.binding!!.tvDiscount)
        val rnd = Random();
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        if (color.equals(mContext.resources.getColor(R.color.colorBlack))) {
            //mContext.baseActivity.showToastError("black")
//            holder.binding!!.tvDiscount.setTextColor(Color.WHITE)
//            holder.binding!!.tvDiscount.setBackgroundTintList(
//                mContext.getResources().getColorStateList(
//                    R.color.colorWhite
//                )
//            )
//
//            holder.binding!!.tvDiscount.setTextColor(Color.WHITE)
        } else {
            //  holder.binding!!.tvDiscount.setBackgroundTintList(ColorStateList.valueOf(color)/*mContext.getResources().getColorStateList(R.color.colorOrange)*/)

        }

        if (addressList[position].description!!.contains("div") || addressList[position].description!!.contains(
                "</"
            )
        ) {
            holder.binding!!.tvPromo.setText(Html.fromHtml(addressList[position].description.toString()))
        } else {
            holder.binding!!.tvPromo.setText(
                addressList[position].description
            )
        }

        holder.binding.viewDetail.setText(addressList[position].discount + "% off")

        holder.binding.viewDetailButton.setOnClickListener {
            showOfferInformation(position)
        }
        //holder.binding!!.rBar.setRating(addressList[position].rating?.toFloat())
        Glide.with(mContext)
            .load(addressList[position].icon)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_dummy)
            .into(holder.binding.imgPromo)
        /* holder.binding!!.btnApply.setOnClickListener {
             mContext.callApplyCouponApi(addressList[position].code!!)
         }*/

    }

    override fun getItemCount() : Int {
        return addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : PromoCodeItemBinding?,
        mContext : PromoCodeActivity,
        addressList : ArrayList<PromoCodeListResponse.Body>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }

    var confirmationDialog : Dialog? = null

    public fun showOfferInformation(pos : Int) {
        confirmationDialog = Dialog(activity, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.layout_offer_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)
        val imgOffer = confirmationDialog?.findViewById<ImageView>(R.id.imgOffer)
        //imgOffer!!.visibility = View.GONE
        val txtCouponName = confirmationDialog?.findViewById<TextView>(R.id.txtCouponName)
        val relatiiveParent = confirmationDialog?.findViewById<RelativeLayout>(R.id.relatiiveParent)
       // relatiiveParent!!.visibility = View.GONE
        val txtCouponCode = confirmationDialog?.findViewById<TextView>(R.id.txtCouponCode)
        val txtCouponDiscount = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDiscount)
        val txtCouponDesc = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDesc)
        val layoutBottomSheet =
            confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)
        val animation = AnimationUtils.loadAnimation(activity!!, R.anim.anim)
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()

        txtCouponName!!.setText("Offer Name: " + addressList[pos].name)
        txtCouponCode!!.setText(addressList[pos].code)
        txtCouponDesc!!.setText(Html.fromHtml(addressList[pos].description).toString())
        txtCouponDiscount!!.setText(addressList[pos].discount + "% OFF")
        Glide.with(activity!!).load(addressList[pos].icon).placeholder(R.drawable.ic_dummy)
            .into(imgOffer!!)
        btnSubmit?.setOnClickListener {
            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        confirmationDialog?.dismiss()
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        when (mKey) {
            "Clear Cart" -> confirmationDialog?.dismiss()
        }
    }

}