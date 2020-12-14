package com.example.services.socket

interface SocketInterface {
    fun onSocketCall(onMethadCall : String, vararg args : Any)

    fun onSocketConnect(vararg args : Any)

    fun onSocketDisconnect(vararg args : Any)
}
