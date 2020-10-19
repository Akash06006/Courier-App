package com.example.courier.chatSocket;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;


import com.example.courier.constants.GlobalConstants;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;


/**
 * This class deals with connection creation, connection callbacks, message listeners map maintenance
 */
public class SocketConnectionManager {
    private static final SocketConnectionManager INSTANCE = new SocketConnectionManager();
    private ConnectionListener connectionListener;
    private HashMap<String, Emitter.Listener> customEmitterListenerMap = new HashMap<>();

    private enum CONNECTION_STATE {DISCONNECTED, CONNECTED}

    private CONNECTION_STATE connectionState = CONNECTION_STATE.DISCONNECTED;

    private SocketConnectionManager() {

    }

    public static SocketConnectionManager getInstance() {
        return INSTANCE;
    }

    private Socket socket;

    public void createConnection(ConnectionListener connectionListener, @NonNull HashMap<String, Emitter.Listener> customEmitterListerMap)
            throws URISyntaxException {
        this.connectionListener = connectionListener;
        this.customEmitterListenerMap = customEmitterListerMap;
        /*
        We can pass connection specific options from here like reconnection, num of reconnect attempts etc
        null value keeps the default options (includes reconnection set to true)
         */
        socket = IO.socket(GlobalConstants.getSOCKET_CHAT_URL(), null);
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_PING, onPing);
        socket.on(Socket.EVENT_PONG, onPong);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
        socket.on(Socket.EVENT_CONNECTING, onConnecting);
        socket.on(Socket.EVENT_ERROR, onError);
        socket.on(Socket.EVENT_RECONNECT, onReconnect);
        socket.on(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
        socket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);
        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, onReconnectAttempt);
        socket.on(Socket.EVENT_RECONNECTING, onReconnecting);
        socket.connect();
    }

    public Socket getSocket() {
        return socket;
    }

    public CONNECTION_STATE getConnectionState() {
        return connectionState;
    }

    /**
     * Add any emitter listener from your code. These are kept in a map here and will be cleared while closing the socket connection
     *
     * @param eventName
     * @param listener
     */
    public void addEventListener(String eventName, Emitter.Listener listener) {
        if (customEmitterListenerMap.containsKey(eventName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                customEmitterListenerMap.replace(eventName, listener);
            } else {
                customEmitterListenerMap.remove(eventName);
                customEmitterListenerMap.put(eventName, listener);
            }
        } else
            customEmitterListenerMap.put(eventName, listener);
        socket.on(eventName, listener);
    }

    public void removeEventListener(String eventName) {
        customEmitterListenerMap.remove(eventName);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (connectionListener != null)
                connectionListener.onConnected();
            connectionState = CONNECTION_STATE.CONNECTED;
        }
    };
    private Emitter.Listener onPing = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onPing");
        }
    };
    private Emitter.Listener onPong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onPong");
            logLong(args);
        }
    };
    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onConnectTimeout");
            log(args);
        }
    };
    private Emitter.Listener onConnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onConnecting");
        }
    };
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onError");
            try {
                EngineIOException error = (EngineIOException) args[0];
                Log.e("SCM", "error: " + error.transport + " ,code: " + error.code);
            } catch (Exception e) {
                Log.e("SCM", "exception: " + e.getMessage());
            }
        }
    };
    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onReconnect");
            logInt(args);
        }
    };
    private Emitter.Listener onReconnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onReconnectError");
            try {
                SocketIOException error = (SocketIOException) args[0];
                Log.e("SCM", "error: " + error.getMessage());
            } catch (Exception e) {
                Log.e("SCM", "exception: " + e.getMessage());
            }
        }
    };
    private Emitter.Listener onReconnectFailed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onReconnectFailed");
            logInt(args);
        }
    };
    private Emitter.Listener onReconnectAttempt = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onReconnectAttempt");
            logInt(args);
        }
    };
    private Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SCM", "onReconnecting");
            logInt(args);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            logString(args);
            try {
                String data = (String) args[0];
                if (data.equalsIgnoreCase(socket.id())) {
                    if (connectionListener != null)
                        connectionListener.onDisconnected();
                    connectionState = CONNECTION_STATE.DISCONNECTED;
                }
            } catch (Exception e) {
                Log.e("SCM", "exception:" + e);
            }

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            logString(args);
            if (connectionListener != null)
                connectionListener.onConnectError();
            connectionState = CONNECTION_STATE.DISCONNECTED;
        }
    };

    public void closeConnection() {
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        for (Map.Entry<String, Emitter.Listener> entries : customEmitterListenerMap.entrySet()) {
            socket.off(entries.getKey(), entries.getValue());
        }
        customEmitterListenerMap.clear();
        socket.disconnect();
    }

    private void log(Object... args) {
        Log.e("SCM", "Socket: " + socket.id());
        try {
            JSONObject data = (JSONObject) args[0];
            Log.e("SCM", "data: " + data.toString());
        } catch (Exception e) {
            Log.e("SCM", "exception: " + e.getMessage());
        }
    }

    private void logInt(Object... args) {
        Log.e("SCM", "Socket: " + socket.id());
        try {
            Integer data = (Integer) args[0];
            Log.e("SCM", "data: " + data.toString());
        } catch (Exception e) {
            Log.e("SCM", "exception: " + e.getMessage());
        }
    }

    private void logLong(Object... args) {
        Log.e("SCM", "Socket: " + socket.id());
        try {
            Long data = (Long) args[0];
            Log.e("SCM", "data: " + data.toString());
        } catch (Exception e) {
            Log.e("SCM", "exception: " + e.getMessage());
        }
    }

    private void logString(Object... args) {
        Log.e("SCM", "Socket: " + socket.id());
        try {
            String data = (String) args[0];
            Log.e("SCM", "data: " + data);
        } catch (Exception e) {
            Log.e("SCM", "exception: " + e.getMessage());
        }
    }
}
