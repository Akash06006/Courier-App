package com.example.courier.chatSocket;

public interface ConnectionListener {
    public void onConnected();

    public void onDisconnected();

    public void onConnectError();
}