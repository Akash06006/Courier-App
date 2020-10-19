package com.example.services.chatSocket

interface SocketChatInterface {
    fun onSocketCall(onMethadCall : String, vararg args : Any)

    fun onSocketConnect(vararg args : Any)

    fun onSocketDisconnect(vararg args : Any)
}
