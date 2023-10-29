package com.rashad.websocket.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.rashad.websocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {

//    private final SocketIOServer socketIOServer;
    private final SocketIONamespace socketIONamespace;

    public SocketModule(SocketIOServer socketIOServer) {
        this.socketIONamespace = socketIOServer.addNamespace("/websocket");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener("send_message", Message.class, onMessageReceived());
    }

    private DataListener<Message> onMessageReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(String.format("%s -> %s", senderClient.getSessionId(), data.getContent()));
            socketIONamespace.getAllClients().forEach(
                    x -> {
                        if (!x.getSessionId().equals(senderClient.getSessionId())) {
                            x.sendEvent("get_message", data);
                        }
                    }
            );
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info(String.format("SocketID: %s connected", client.getSessionId()));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info(String.format("SocketID: %s disconnected", client.getSessionId()));
        };
    }
}











