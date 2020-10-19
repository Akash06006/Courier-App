package com.example.courier.utils

/*
 * Created by saira on 22-12-2017.
 */


import android.app.Dialog
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import com.example.courier.R
import com.example.courier.callbacks.ChoiceCallBack

class DialogClass {
    private var checkClick = 0

    fun setDefaultDialog(
        mContext : Context,
        mInterface : DialogssInterface,
        mKey : String,
        mTitle : String
    ) : Dialog {
        val dialogView = Dialog(mContext)
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(mContext),
                R.layout.custom_dialog,
                null,
                false
            )

        dialogView.setContentView(binding.root)
        dialogView.setCancelable(false)
        dialogView.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialogView.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yes = dialogView.findViewById<Button>(R.id.yes)
        val no = dialogView.findViewById<Button>(R.id.no)
        val tvLogout = dialogView.findViewById<TextView>(R.id.tv_dialog_logout)
        if (mKey.equals("logout"))
            tvLogout.visibility = View.VISIBLE
        else
            tvLogout.visibility = View.GONE

        if (!ValidationsClass().checkStringNull(mTitle))
            (dialogView.findViewById<View>(R.id.txt_dia) as TextView).text = mTitle
//        (dialogView.findViewById<View>(R.id.txt_dia) as TextView).text = mTitle
        /* if (!ValidationsClass().checkStringNull(submitString))
             yes.visibility = View.VISIBLE*/
        yes.setOnClickListener {
            mInterface.onDialogConfirmAction(null, mKey)
        }
        /* if (!ValidationsClass().checkStringNull(cancelString))
             no.visibility = View.VISIBLE*/
        no.setOnClickListener {
            mInterface.onDialogCancelAction(null, mKey)
        }
        // Create the AlertDialog object and return it
        return dialogView

    }

    fun setRatingtDialog(
        mContext : Context,
        mInterface : DialogssInterface,
        mKey : String
    ) : Dialog {
        val dialogView = Dialog(mContext, R.style.transparent_dialog_borderless)
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(mContext),
                R.layout.custom_rating_dialog,
                null,
                false
            )


        dialogView.setContentView(binding.root)
        dialogView.setCancelable(false)

        dialogView.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogView.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val submit = dialogView.findViewById<Button>(R.id.submit)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val customEditText = dialogView.findViewById<EditText>(R.id.et_custom_amount)
        val customText = dialogView.findViewById<TextView>(R.id.tv_addcustom)

        customText.setOnClickListener {
            if (checkClick == 0) {
                customEditText.visibility = View.VISIBLE
                checkClick = 1
            } else {
                customEditText.visibility = View.GONE
                checkClick = 0
            }
        }

        submit.setOnClickListener {
            mInterface.onDialogConfirmAction(null, mKey)
        }


        ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener(function = { ratingBar, rating, fromUser->
                if (rating < 1.0f)
                    ratingBar.rating = 1.0f
            })



        return dialogView

    }

    fun setConfirmationDialog(
        mContext : Context,
        mInterface : DialogssInterface,
        mKey : String
    ) : Dialog {
        val dialogView = Dialog(mContext, R.style.transparent_dialog_borderless)
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(mContext),
                R.layout.layout_confirmation_popup,
                null,
                false
            )


        dialogView.setContentView(binding.root)
        dialogView.setCancelable(false)

        dialogView.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogView.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val submit = dialogView.findViewById<Button>(R.id.btn_continue)


        submit.setOnClickListener {
            mInterface.onDialogConfirmAction(null, mKey)
        }


        return dialogView

    }

    fun setCancelDialog(
        mContext : Context,
        mInterface : DialogssInterface,
        mKey : String
    ) : Dialog {
        val dialogView = Dialog(mContext, R.style.transparent_dialog_borderless)
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(mContext),
                R.layout.layout_cancel_popup,
                null,
                false
            )


        dialogView.setContentView(binding.root)
        dialogView.setCancelable(false)

        dialogView.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogView.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val submit = dialogView.findViewById<Button>(R.id.btn_continue)
        val cancel = dialogView.findViewById<Button>(R.id.btn_back)

        submit.setOnClickListener {
            mInterface.onDialogConfirmAction(null, mKey)
        }
        cancel.setOnClickListener {
            mInterface.onDialogCancelAction(null, mKey)
        }




        return dialogView
    }

    fun setUploadConfirmationDialog(
        mContext : Context,
        mInterface : ChoiceCallBack,
        mKey : String
    ) : Dialog {
        val dialogView = Dialog(mContext)
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(mContext),
                R.layout.dialog_image_choice,
                null,
                false
            )

        dialogView.setContentView(binding.root)
        dialogView.setCancelable(true)
        dialogView.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialogView.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val camera = dialogView.findViewById<LinearLayout>(R.id.ll_camera)
        val gallery = dialogView.findViewById<LinearLayout>(R.id.ll_gallery)
        // Create the AlertDialog object and return it
        camera.setOnClickListener {
            mInterface.photoFromCamera(mKey)
            dialogView.dismiss()
        }
        gallery.setOnClickListener {
            mInterface.photoFromGallery(mKey)
            dialogView.dismiss()
        }



        dialogView.show()

        return dialogView

    }

}
