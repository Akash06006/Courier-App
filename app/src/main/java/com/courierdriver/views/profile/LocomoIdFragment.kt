package com.courierdriver.views.profile

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.FragmentLocomoIdBinding
import com.courierdriver.model.LocomoIdModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DownloadFileCallback
import com.courierdriver.utils.DownloadTask
import com.courierdriver.viewmodels.profile.ProfileViewModel
import java.io.File

class LocomoIdFragment : BaseFragment(), DownloadFileCallback {
    private var binding: FragmentLocomoIdBinding? = null
    private var viewModel: ProfileViewModel? = null
    private var downloadUrl = ""
    private var model: LocomoIdModel.Body? = null
    private var mDialogClass: DialogClass? = DialogClass()
    private var dialogImagePreview: Dialog? = null

    override fun initView() {
        binding = viewDataBinding as FragmentLocomoIdBinding
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding!!.viewModel = viewModel
        sharedPrefValue()
        getProfileDetailsObserver()
        loaderObserver()
        viewClicks()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "img_view" -> {
                        showImagePreviewDialog()
                    }
                    "img_download" -> {
                        downloadImageNew("Locomo id",downloadUrl)
                      //  DownloadTask(baseActivity, downloadUrl, this)
                    }
                }
            })
        )
    }

    private fun showImagePreviewDialog() {
        dialogImagePreview = Dialog(baseActivity)
        dialogImagePreview!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(baseActivity),
                R.layout.dialog_image_preview,
                null,
                false
            )
        dialogImagePreview!!.setContentView(dialogBinding.root)
        dialogImagePreview!!.setCancelable(false)

        val imgPreview =
            dialogImagePreview!!.findViewById<ImageView>(R.id.img_preview)
        val imgCross =
            dialogImagePreview!!.findViewById<ImageView>(R.id.img_cross)

        Glide.with(baseActivity)
            .load(model!!.locomoIdUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading_image)
            .into(imgPreview)


        imgCross.setOnClickListener {
            dialogImagePreview!!.dismiss()
        }

        dialogImagePreview!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialogImagePreview!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogImagePreview!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogImagePreview!!.show()
    }


    private fun loaderObserver() {
        viewModel!!.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    private fun sharedPrefValue() {
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()
        binding!!.image = userImage
    }

    private fun getProfileDetailsObserver() {
        viewModel!!.locomoId("6")
        viewModel!!.locomoIdGetData().observe(this,
            Observer<LocomoIdModel> { response ->
                baseActivity.stopProgressDialog()
                binding!!.linInProgress.visibility = View.GONE
                binding!!.linLocomoId.visibility = View.VISIBLE
                binding!!.linLocomoIdDetail.visibility = View.VISIBLE
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            //UtilsFunctions.showToastSuccess(response.message!!)
                            binding!!.model = response.body
                            model = response.body
                            downloadUrl = response.body!!.locomoIdUrl!!
                            setProfileImage(response.body.image!!)
                        }
                        208 -> {
                            binding!!.linInProgress.visibility = View.VISIBLE
                            binding!!.linLocomoIdDetail.visibility = View.GONE

                            model = response.body
                            setProfileImage(response.body!!.image!!)
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    binding!!.linInProgress.visibility = View.VISIBLE
                    binding!!.linLocomoId.visibility = View.GONE
                    // UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun setProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_user_profile)
            .into(binding!!.imgProfile)
    }

    private fun downloadImageNew(
        filename: String,
        downloadUrlOfImage: String
    ) {
        try {
            val dm =
                baseActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request: DownloadManager.Request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator + filename + ".jpg"
                )
            dm.enqueue(request)

            baseActivity.showToastSuccess("Image downloaded successfully")
            //Toast.makeText(baseActivity, "Image download started.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            baseActivity.showToastError("Image download failed")
            //Toast.makeText(baseActivity, "Image download failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showNotification(downloadFileName: String) {
        val SDCardRoot = Environment.getExternalStorageDirectory()
            .toString() + "/" + "GoodsDelivery" + downloadFileName
        //  var SDCardRoot = Environment.getExternalStorageDirectory()
        val file = File(SDCardRoot)
        val selectedUri = FileProvider.getUriForFile(
            baseActivity,
            baseActivity.packageName + ".fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(selectedUri, "image/*") // here we set correct type for PDF
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        // val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            baseActivity, 1, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = NotificationCompat.Builder(baseActivity, baseActivity.packageName)
                .setSmallIcon(R.drawable.ic_app)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        baseActivity.resources,
                        R.drawable.ic_app
                    )
                )
                .setContentTitle(getString(R.string.app_name))
                .setChannelId(baseActivity.packageName)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_app
                    )
                )
                .setContentText("Downloaded successfully")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.FLAG_HIGH_PRIORITY)

            val notificationManager =
                baseActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    baseActivity.packageName,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        } else {
            notificationBuilder = NotificationCompat.Builder(baseActivity, baseActivity.packageName)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        baseActivity.resources,
                        R.drawable.ic_app
                    )
                )
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Downloaded successfully")
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder!!.setSmallIcon(R.drawable.ic_app)
        } else {
            notificationBuilder!!.setSmallIcon(R.drawable.ic_app)
        }
        val notificationManager = NotificationManagerCompat.from(baseActivity)
        notificationBuilder.priority = Notification.PRIORITY_MAX
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_locomo_id
    }
}