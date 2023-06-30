package io.werk24;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import io.werk24.model.W24PresignedPost;

/**
 * TechreadClientHttps uses HTTP protocol for communication.
 */
public class TechreadClientHttps {
    private AuthClient authClient;
    // We use a CloseableHttpClient for executing HTTP requests
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public TechreadClientHttps() {
    }

    /**
     * Method to associate an AuthClient to this TechreadClientHttps instance
     *
     * @param authClient AuthClient instance for authentication
     */
    public void registerAuthClient(AuthClient authClient) {
        this.authClient = authClient;
    }

    /**
     * Method to upload a file to a specified destination
     *
     * @param presignedPost object containing information about the destination and the associated fields
     * @param content       binary content of the file
     * @throws IOException if an error occurs during uploading
     */
    public void uploadAssociatedFile(
        W24PresignedPost presignedPost,
        byte[] content
    ) throws IOException {
        // If there's no content, we cannot upload anything
        if(content == null) {
            return;
        }

        // MultipartEntityBuilder is used for constructing the multipart POST request
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // Loop over the fields in the presigned post and add them to the multipart request
        for(String key: presignedPost.getFields().keySet()) {
            builder.addTextBody(key, presignedPost.getFields().get(key));
        }

        // Add the actual content to the request
        builder.addBinaryBody("file", content, ContentType.APPLICATION_OCTET_STREAM, "filename");

        // Prepare the HTTP POST request
        HttpPost httpPost = new HttpPost(presignedPost.getUrl());
        httpPost.setEntity(builder.build());

        // Execute the request and check the response status
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int status = response.getStatusLine().getStatusCode();
            if (status < 200 || status > 300) {
                throw new IOException("Unexpected response status: " + status);
            }
        }
    }

    /**
     * Method to download content from a specified URL
     *
     * @param payloadUrl    URL to download the content from
     * @return              downloaded content as a byte array
     * @throws IOException if an error occurs during downloading
     */
    public byte[] downloadPayload(String payloadUrl) throws IOException {
        // Prepare the HTTP GET request
        HttpGet httpGet = new HttpGet(payloadUrl);

        // Execute the request and retrieve the response
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            // If the response contains a content, return it as a byte array
            if (entity != null) {
                return EntityUtils.toByteArray(entity);
            } else {
                throw new IOException("Empty response");
            }
        }
    }

    /**
     * Method to close the http client. Should be called when the client is no longer needed.
     *
     * @throws IOException if an error occurs during closing the client
     */
    public void close() throws IOException {
        httpClient.close();
    }
}
