package io.werk24;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.werk24.model.W24TechreadAction;
import io.werk24.model.W24TechreadCommand;
import io.werk24.model.W24TechreadMessage;

public class TechreadClientWss extends WebSocketClient {

    private AuthClient authClient;
    private ObjectMapper mapper;
    private final Object lock = new Object();
    private W24TechreadMessage receivedMessage = null;

    public TechreadClientWss(URI uri) {
        super(uri,new Draft_6455());
        this.mapper = new ObjectMapper()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
    }

    /**a
     * Register the AuthClient to the TechreadClientWss
     * @param authClient Authentication client to get a recent token.
     */
    public void registerAuthClient(AuthClient authClient) {
        this.authClient = authClient;
        super.addHeader("Authorization", "Bearer " + authClient.getToken());
    }

    /**
     * Called when the websocket connection is established.
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    /**
     * Called when the websocket connection is closed.
     *
     * TODO: implement handing of close codes
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }

    /**
     * Called when the websocket connection receives a message.
     *
     * @param message The message received from the server.
     *  This should be in the W24TechreadMessage format.
     */
    @Override
    public void onMessage(String message) {
        synchronized (lock) {
            if (this.isClosing() || this.isClosed()) {
                return;
            }

            try{
                this.receivedMessage = mapper.readValue(
                    message,
                    W24TechreadMessage.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            this.lock.notifyAll();
        }
    }

    /**
     * Wait until a message is received.
     * @return W24TechreadMessage The received message.
     */
    public W24TechreadMessage waitForMessage() {
        synchronized (lock) {
            while (this.receivedMessage == null && !this.isClosed() && !this.isClosing()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(this.isClosed() || this.isClosing()){
                return null;
            }

            W24TechreadMessage result = this.receivedMessage;
            this.receivedMessage = null;
            return result;
        }
    }

    /**
     * Called when the websocket connection has an error.
     */
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Send an unformatted message to the server.
     * @param message The message to send.
     */
    private void sendMessage(String message) {
        if (isOpen()) {
            send(message);
        }
    }

    public W24TechreadMessage sendCommand(W24TechreadAction action, Map<String, Object> message) {
        W24TechreadCommand command = new W24TechreadCommand(action, message);
        try{
            sendMessage(mapper.writeValueAsString(command));
        } catch (JsonProcessingException e){
            // TODO: handle exception
        }
        return waitForMessage();


    }
}
