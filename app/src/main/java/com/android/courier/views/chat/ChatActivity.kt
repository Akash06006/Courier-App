package com.android.courier.views.chat

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.android.courier.adapters.chat.MessageListAdapter
import com.android.courier.application.MyApplication
import com.android.courier.chatSocket.ConnectionListener
import com.android.courier.chatSocket.SocketConnectionManager
import com.android.courier.constants.GlobalConstants
import com.android.courier.constants.GlobalConstants.SOCKET_CHAT_URL
import com.android.courier.utils.ConvertBase64
import com.google.gson.GsonBuilder
import com.uniongoods.adapters.BoatChatMessageListAdapter
import io.socket.emitter.Emitter
import org.json.JSONArray
import com.android.courier.databinding.ActivityChatBinding
import com.android.courier.model.chat.ChatListModel
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : BaseActivity(),
    ConnectionListener {
    private val sharedPrefClass = SharedPrefClass()
    private var chatList : ArrayList<ChatListModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    lateinit var chatBinding : ActivityChatBinding
    private var mMessageAdapter : MessageListAdapter? =
        null
    private var boatMessageAdapter : BoatChatMessageListAdapter? = null
    val RC_CODE_PICKER = 2000
    var images : MutableList<com.esafirm.imagepicker.model.Image> = mutableListOf()
    var licenseImageFile : File? = null
    var selectedImage = ""
    var orderId = ""
    var boatMessageDialog : Dialog? = null
    /*
        var socketConnectionManager: SocketConnectionManager? = null
    */
    override fun getLayoutId() : Int {
        return R.layout.activity_chat
    }

    override fun onResume() {
        super.onResume()
        Log.e("Socket", "onResume")

    }

    override fun initViews() {
        chatBinding = viewDataBinding as ActivityChatBinding
        chatBinding.commonToolBar.imgRight.visibility = View.GONE
        chatBinding.commonToolBar.imgToolbarText.text = "Help"
        chatList = ArrayList()
        orderId = intent.extras?.get("orderId").toString()
        mMessageAdapter = MessageListAdapter(
            this,
            chatList,
            sharedPrefClass
        )
        chatBinding.reyclerviewMessageList.setLayoutManager(LinearLayoutManager(this))
        chatBinding.reyclerviewMessageList.setAdapter(mMessageAdapter)

        Log.e("Socket", " Init")

        try {
            val socketConnectionManager : SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, Emitter.Listener>()
            )
        } catch (e : URISyntaxException) {
            e.printStackTrace()
        }
        //do heavy work on a background thread
        SocketConnectionManager.getInstance()
            .addEventListener("joinRoom") { args->
                val data = args[0] as JSONObject
                try {
                    val roomId = data.getString("groupId")
                    GlobalConstants.ROOM_ID = roomId
                    sharedPrefClass.putObject(this, GlobalConstants.ROOM_ID, roomId)
                    val objectChatHistory = JSONObject()
                    objectChatHistory.put(
                        "authToken", sharedPrefClass.getPrefValue(
                            MyApplication.instance,
                            GlobalConstants.ACCESS_TOKEN
                        ).toString()
                    )
                    objectChatHistory.put(
                        "groupId", GlobalConstants.ROOM_ID/* sharedPrefClass.getPrefValue(
                            MyApplication.instance,
                            GlobalConstants.ROOM_ID
                        ).toString()*/
                    )
                    objectChatHistory.put("userType", "user")
                    SocketConnectionManager.getInstance()
                        .socket.emit(
                        "chatHistory",
                        objectChatHistory
                    )
                    Log.e("Socket", "Room join")

                } catch (e : JSONException) {
                }
                runOnUiThread {
                    // Toast.makeText(this, "roomJoined", Toast.LENGTH_SHORT).show()
                }
            }

        SocketConnectionManager.getInstance()
            .addEventListener("leaveRoom") { args->
                val data = args[0] as JSONObject
                try {
                    sharedPrefClass.removeParticularKey(this, GlobalConstants.ROOM_ID)
                } catch (e : JSONException) {
                }
            }

        SocketConnectionManager.getInstance()
            .addEventListener("chatHistory") { args->
                val data = args[0] as JSONArray
                chatList =
                    (gson.fromJson("" + data, Array<ChatListModel>::class.java)).toCollection(
                        ArrayList()
                    )

                runOnUiThread {
                    if (chatList == null || chatList!!.isEmpty()) {
                        //showBoatChatMessages()
                    }
                    mMessageAdapter!!.setData(chatList)
                    mMessageAdapter!!.notifyDataSetChanged()
                }
            }

        chatBinding.buttonChatboxSend.setOnClickListener {
            if (!chatBinding.edittextChatbox.text.toString().isEmpty()) {
                val objectChatHistory = JSONObject()
                objectChatHistory.put(
                    "authToken", sharedPrefClass.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.ACCESS_TOKEN
                    ).toString()
                )
                objectChatHistory.put(
                    "groupId", GlobalConstants.ROOM_ID/*sharedPrefClass.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.ROOM_ID
                    ).toString()*/
                )
                objectChatHistory.put("type", 1)
                objectChatHistory.put("message", chatBinding.edittextChatbox.text.toString())
                objectChatHistory.put("userType", "user")
                objectChatHistory.put("receiverId", GlobalConstants.ADMIN_ID)
                objectChatHistory.put(
                    "orderId",
                    orderId/*GlobalConstants.ADMIN_ID*/
                )
                SocketConnectionManager.getInstance()
                    .socket.emit("sendMessage", objectChatHistory)

                chatBinding.edittextChatbox.setText("")
                Log.e("Socket", "Message Sent")
            } else {
                showToastError("Please enter message")
            }
        }

        SocketConnectionManager.getInstance()
            .addEventListener("newMessage") { args->
                val data = args[0] as JSONObject
                Log.e("Socket", "New Message")
                runOnUiThread {
                    chatList!!.add(
                        gson.fromJson<ChatListModel>(
                            "" + data,
                            ChatListModel::class.java
                        )
                    )
                    mMessageAdapter!!.setData(chatList)
                    mMessageAdapter!!.notifyDataSetChanged()
                    // chatBinding.reyclerviewMessageList.scrollToPosition(chatList?.size?.minus(1))
                }
            }

        chatBinding.buttonSelectImage.setOnClickListener {
            pickImage()
        }
    }

    override fun onConnectError() {
        Log.e("Socket", "Error Connected")
        runOnUiThread {
            //Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConnected() {
        Log.e("Socket", "OnConnected")
        runOnUiThread {
            val objectCreateRoom = JSONObject()
            objectCreateRoom.put(
                "authToken", sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
            )
            objectCreateRoom.put("receiverId", GlobalConstants.ADMIN_ID)
            objectCreateRoom.put("orderId", orderId)
            objectCreateRoom.put("userType", "user")
            SocketConnectionManager.getInstance()
                .socket.emit("joinRoom", objectCreateRoom)
            // Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDisconnected() {
        runOnUiThread {
            Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val objectChatHistory = JSONObject()
        objectChatHistory.put(
            "authToken", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ACCESS_TOKEN
            ).toString()
        )
        objectChatHistory.put(
            "groupId", GlobalConstants.ROOM_ID /*sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ROOM_ID
            ).toString()*/
        )
        SocketConnectionManager.getInstance()
            .socket.emit("leaveRoom", objectChatHistory)
        val socketConnectionManager =
            SocketConnectionManager.getInstance()
        socketConnectionManager.closeConnection()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        /* val socketConnectionManager = SocketConnectionManager.getInstance()
         socketConnectionManager.closeConnection()*/
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val socketConnectionManager : SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, Emitter.Listener>()
            )
        } catch (e : URISyntaxException) {
            e.printStackTrace()
        }
        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = ImagePicker.getImages(data)

            licenseImageFile = File(images.get(0).path)
            selectedImage = images.get(0).name
            showImageData(true, images.get(0).path)
        }
    }

    private fun pickImage() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            .folderMode(true) // set folder mode (false by default)
            .single()
            .limit(1)
            .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
            .toolbarImageTitle(getString(R.string.gallery_select_title_msg))
            .start(RC_CODE_PICKER)
    }

    fun showImageData(isThrowSelection : Boolean, imagePathOrURL : String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.show_image_dialog)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent)
        // set the custom dialog components - text, image and button
        // set the custom dialog components - text, image and button
        val image : ImageView = dialog.findViewById(R.id.img) as ImageView
        val btnCancel : Button = dialog.findViewById(R.id.btn_cancel) as Button
        val btnSend : Button = dialog.findViewById(R.id.btn_send) as Button

        if (isThrowSelection) {
            Glide.with(this)
                .load(imagePathOrURL) //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .placeholder(R.drawable.ic_dummy)
                .into(image)
            btnSend.visibility = View.VISIBLE
        } else {
            Glide.with(this)
                .load(SOCKET_CHAT_URL + "" + imagePathOrURL) //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .placeholder(R.drawable.ic_dummy)
                .into(image)
            btnSend.visibility = View.GONE
        }


        btnSend.setOnClickListener {
            var imageExtension =
                images.get(0).path.substring(images.get(0).path.lastIndexOf(".") + 1)
            val bm =
                BitmapFactory.decodeFile(/*"/path/to/image.jpg"*/images.get(0).path)
            val baos = ByteArrayOutputStream()
            /* bm.compress(
                 Bitmap.CompressFormat.JPEG,
                 100,
                 baos
             ) */// bm is the bitmap object
            var image =
                ConvertBase64.getStringImage(bm)
            val objectChatHistory = JSONObject()
            objectChatHistory.put(
                "authToken", sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
            )
            objectChatHistory.put(
                "groupId", GlobalConstants.ROOM_ID/*sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ROOM_ID
                ).toString()*/
            )
            objectChatHistory.put("type", 2)
            objectChatHistory.put("media", image)
            objectChatHistory.put("extension", imageExtension)
            objectChatHistory.put("userType", "user")

            objectChatHistory.put("receiverId", GlobalConstants.ADMIN_ID)
            objectChatHistory.put(
                "orderId",
                orderId/*GlobalConstants.ADMIN_ID*/
            )
            SocketConnectionManager.getInstance()
                .socket.emit("sendMessage", objectChatHistory)
            val objChatHistory = JSONObject()
            objChatHistory.put(
                "authToken", sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
            )
            objChatHistory.put(
                "groupId", GlobalConstants.ROOM_ID /*sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ROOM_ID
                ).toString()*/
            )
            SocketConnectionManager.getInstance()
                .socket.emit(
                "chatHistory",
                objChatHistory
            )
            Log.e("Socket", "Image Sent")
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBoatChatMessages() {
        chatBinding!!.boatMessageView.visibility = View.VISIBLE
        chatBinding.edittextChatbox.isEnabled = false
        var boatMessageList : ArrayList<String> = ArrayList()
        boatMessageList.add("I have not received my order")
        boatMessageList.add("I have packaging or spillage issue with my order")
        boatMessageList.add("Items are missing or incorrect in my order")
        boatMessageList.add("I have food taste, quality or quantity issue with my order")
        /*  boatMessageDialog = Dialog(this)
          boatMessageDialog!!.setContentView(R.layout.boat_chat_message_dialog)
          boatMessageDialog!!.getWindow().setBackgroundDrawableResource(android.R.color.transparent)
          val wmpl = boatMessageDialog!!.window.attributes
          wmpl.gravity = Gravity.BOTTOM or Gravity.LEFT*/
        // val rvMessages: RecyclerView = boatMessageDialog!!.findViewById(R.id.rvMessages) as RecyclerView
        boatMessageAdapter = BoatChatMessageListAdapter(this, boatMessageList)
        chatBinding.rvMessages.setLayoutManager(LinearLayoutManager(this))
        chatBinding.rvMessages.setAdapter(boatMessageAdapter)
        /*  boatMessageDialog!!.show()
          boatMessageDialog!!.setCancelable(false)*/
    }

    fun onClickBoatMessage(message : String) {
        val objectChatHistory = JSONObject()
        objectChatHistory.put(
            "authToken", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ACCESS_TOKEN
            ).toString()
        )
        objectChatHistory.put(
            "groupId", GlobalConstants.ROOM_ID/* sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ROOM_ID
            ).toString()*/
        )
        objectChatHistory.put("type", 1)
        objectChatHistory.put("message", message)
        objectChatHistory.put("userType", "user")
        objectChatHistory.put(
            "orderId",
            orderId/*GlobalConstants.ADMIN_ID*/
        )
        SocketConnectionManager.getInstance()
            .socket.emit("sendMessage", objectChatHistory)
        chatBinding!!.boatMessageView.visibility = View.GONE
        chatBinding.edittextChatbox.isEnabled = true

    }
}