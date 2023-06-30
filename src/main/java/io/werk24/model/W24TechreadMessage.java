package io.werk24.model;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class W24TechreadMessage {
    private String request_id;
    private W24TechreadMessageType message_type;
    private String message_subtype;
    private int page_number =  0;
    private Map payload_dict = null;
    private String payload_url = null;
    private String payload_bytes = null;
    private List<String> exceptions;
}

