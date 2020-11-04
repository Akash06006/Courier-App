package com.android.courier.chatSocket;

public interface ConnectionListener {
    public void onConnected();

    public void onDisconnected();

    public void onConnectError();
}