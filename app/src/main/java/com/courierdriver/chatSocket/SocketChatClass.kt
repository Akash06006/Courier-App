package com.courierdriver.chatSocket

/*
 * Created by dell on 4/5/2018.
 */

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.services.chatSocket.SocketChatInterface
import com.courierdriver.constants.GlobalConstants
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

internal class SocketChatClass private constructor() {
    private var mSocket: Socket? = null
    // Enter ON Method Name here
    private val mOnMethod = "responseFromServer"
    private var mSocketInterface: SocketChatInterface? = null
    private var isConnected: Boolean? = null
    /**
     * Default Listener
     * Define what you want to do when connection is established
     */
    private val onConnect = Emitter.Listener { args ->
        // Get a handler that can be used to post to the main thread
        Handler(Looper.getMainLooper()).post {
            Log.e(TAG, "connect run")
            isConnected = true
            if (mSocketInterface != null)
                mSocketInterface!!.onSocketConnect(*args)
        }
    }
    /**
     * Default Listener
     * Define what you want to do when connection is disconnected
     */
    private val onDisconnect = Emitter.Listener { args ->
        /**
         *
         * @param args args
         */
        // Get a handler that can be used to post to the main thread
        Handler(Looper.getMainLooper()).post {
            Log.i(TAG, "disconnected")
            Log.e(TAG, "disconnect")
            isConnected = false
            if (mSocketInterface != null)
                mSocketInterface!!.onSocketDisconnect(*args)
        }
    }
    /**
     * Default Listener
     * Define what you want to do when there's a connection error
     */
    private val onConnectError = Emitter.Listener { args ->
        // Get a handler that can be used to post to the main thread
        Handler(Looper.getMainLooper()).post {
            Log.e(TAG, "Run" + args[0])
            Log.e(TAG, "Error connecting")
        }
    }
    /*
     * On Method call backs from server
     * */
    private val socketListner = Emitter.Listener { args ->
        // Get a handler that can be used to post to the main thread
        Handler(Looper.getMainLooper()).post {
            val data = args[0] as JSONObject
            if (mSocketInterface != null)
                mSocketInterface!!.onSocketCall(mOnMethod, *args)
        }
    }

    init {
        try {
            val options = IO.Options()
            options.reconnection = true
            options.reconnectionAttempts = Integer.MAX_VALUE
            Log.e("Constants.BASE_URL", GlobalConstants.SOCKET_URL)
            mSocket = IO.socket(GlobalConstants.SOCKET_URL, options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

    }

    fun updateSocketInterface(mSocketInterface: SocketChatInterface) {
        this.mSocketInterface = mSocketInterface
    }

    /**
     * Call this method in onCreate and onResume
     */
    fun onConnect() {
        if (!mSocket!!.connected()) {
            mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
            mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
            mSocket!!.on(mOnMethod, socketListner)

            mSocket!!.connect()
        }
    }

    /**
     * Call this method in onPause and onDestroy
     */
    fun onDisconnect() {
        mSocket!!.disconnect()
        mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket!!.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket!!.off(mOnMethod, socketListner)

    }

    /*
     * Send Data to server by use of socket
     * */
    fun sendDataToServer(methodName: String, mObject: Any) {
        // Get a handler that can be used to post to the main thread
        Handler(Looper.getMainLooper()).post {
            mSocket!!.emit("socketFromClient", mObject)
            Log.e("Emit Method", "$methodName Object$mObject")
        }
    }

    /*
     * Interface for Socket Callbacks
     * */
    companion object {
        private var mSocketClass: SocketChatClass? = null
        private val TAG = SocketChatClass::class.java.canonicalName
        val socket: SocketChatClass
            get() {
                if (mSocketClass == null)
                    mSocketClass = SocketChatClass()
                return mSocketClass as SocketChatClass
            }
    }
}
