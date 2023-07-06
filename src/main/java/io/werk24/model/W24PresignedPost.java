package io.werk24.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data public class W24PresignedPost {
    /**
     * Details of the presigned post that allow you to upload
     * a file to our file system.
     */

    /**
     * URL to which the file shall be uploaded.
     */
    private String url;

    /**
     * Fields that need to be added to the form.
     */
    @JsonProperty("fields")
    private Map<String, String> fields = new HashMap<>();
}
