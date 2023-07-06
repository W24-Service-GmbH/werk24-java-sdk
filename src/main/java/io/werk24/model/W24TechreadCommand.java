package io.werk24.model;
import lombok.Data;
import java.util.Map;

@Data public class W24TechreadCommand {
    /**
     * Command that is sent from the client to the Server
     */

    /**
     * Action that shall be performed by the server.
     */
    private final W24TechreadAction action;

    /**
     * Message that shall be processed by the server.
     */
    private final Map message;
}
