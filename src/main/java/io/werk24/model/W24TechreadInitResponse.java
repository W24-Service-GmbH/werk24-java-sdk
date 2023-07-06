package io.werk24.model;
import lombok.Data;

@Data public class W24TechreadInitResponse extends W24TechreadBaseResponse {
    /**
     * API response to the Initialize request
     */

    /**
     * Presigned Post for uploading the drawing
     */
    W24PresignedPost drawing_presigned_post;

    /**
     * Presigned Post for uploading the model
     */
    W24PresignedPost model_presigned_post;
}
