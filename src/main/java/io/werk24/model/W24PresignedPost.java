package io.werk24.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class W24PresignedPost {

    private String url;

    @JsonProperty("fields")
    private Map<String, String> fields = new HashMap<>();
}
