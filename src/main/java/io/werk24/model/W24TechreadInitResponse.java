package io.werk24.model;
import lombok.Data;

@Data public class W24TechreadInitResponse extends W24TechreadBaseResponse {
    W24PresignedPost drawing_presigned_post;
    W24PresignedPost model_presigned_post;
}
