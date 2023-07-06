package io.werk24;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import io.werk24.model.*;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/**
 * TechreadClient is responsible for reading technical drawings.
 */
public class TechreadClient {
    private TechreadClientHttps techreadClientHttps;
    private TechreadClientWss techreadClientWss;
    private ObjectMapper objectMapper;

    public TechreadClient(){
        this.techreadClientHttps = new TechreadClientHttps();
        this.techreadClientWss = new TechreadClientWss(getWebsocketUri());
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor to initialize TechreadClient with license file content.
     *
     * @param licenseFileContent The license file content.
     */
    public TechreadClient(String licenseFileContent) {
        // Parse the license file content and initialize TechreadClient
        Map<String, String> licenseFile = parseDotenv(licenseFileContent);
        this.techreadClientHttps = new TechreadClientHttps();
        this.techreadClientWss = new TechreadClientWss(getWebsocketUri());
        this.objectMapper = new ObjectMapper();

        // Register client with parsed license information
        this.register(
            licenseFile.get("W24TECHREAD_AUTH_REGION"),
            licenseFile.get("W24TECHREAD_AUTH_IDENTITY_POOL_ID"),
            licenseFile.get("W24TECHREAD_AUTH_USER_POOL_ID"),
            licenseFile.get("W24TECHREAD_AUTH_CLIENT_ID"),
            licenseFile.get("W24TECHREAD_AUTH_CLIENT_SECRET"),
            licenseFile.get("W24TECHREAD_AUTH_USERNAME"),
            licenseFile.get("W24TECHREAD_AUTH_PASSWORD")
        );
    }

    private URI getWebsocketUri(){
        try{
            return new URI("wss://ws-api.w24.co/v2");
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses dotenv content and returns a map of key-value pairs.
     *
     * @param dotenvContent Content in dotenv format.
     * @return Map of key-value pairs.
     */
    public static Map<String, String> parseDotenv(String dotenvContent) {
        return Arrays.stream(dotenvContent.split("\n"))
                .filter(line -> line.contains("=")) // filter out lines without '='
                .map(line -> line.split("=", 2))
                .collect(Collectors.toMap(
                    arr -> arr[0].trim(),
                    arr -> {
                        if (arr.length > 1) {
                            String val = arr[1].trim();
                            if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                                // remove surrounding quotes
                                return val.substring(1, val.length() - 1);
                            }
                            return val;
                        }
                        return "";
                    }
                ));
    }

    /**
     * Registers the client with AWS Cognito using the given credentials.
     *
     * @param cognitoRegion AWS Cognito region.
     * @param cognitoIdentityPoolId AWS Cognito Identity Pool ID.
     * @param cognitoUserPoolId AWS Cognito User Pool ID.
     * @param cognitoClientId AWS Cognito Client ID.
     * @param cognitoClientSecret AWS Cognito Client Secret.
     * @param username AWS Cognito username.
     * @param password AWS Cognito password.
     */
    public void register(
        String cognitoRegion,
        String cognitoIdentityPoolId,
        String cognitoUserPoolId,
        String cognitoClientId,
        String cognitoClientSecret,
        String username,
        String password
    ) {
        // Create and register an auth client
        AuthClient authClient = new AuthClient(
            cognitoRegion,
            cognitoIdentityPoolId,
            cognitoUserPoolId,
            cognitoClientId,
            cognitoClientSecret);
        authClient.register(username, password);

        // Login to AWS Cognito with registered auth client
        try {
            authClient.login();
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        }

        // Register auth client with HTTPS and WSS clients
        techreadClientHttps.registerAuthClient(authClient);
        techreadClientWss.registerAuthClient(authClient);
    }

    /**
     * Reads a drawing.
     *
     * @param drawingBytes The bytes of the drawing.
     * @param asks The list of queries.
     * @param model The model of the drawing.
     * @param maxPages The maximum number of pages to read.
     * @param drawingFilename The filename of the drawing.
     */
    public Map<String,Object> readDrawing(
        byte[] drawingBytes,
        List<W24Ask> asks,
        byte[] model,
        int maxPages,
        String drawingFilename
    ) {
        // Connect to WebSocket and handle potential errors
        try {
            this.techreadClientWss.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize the techread request and upload the drawing
        W24TechreadInitResponse initResponse = sendInitCommand(asks, maxPages, drawingFilename);
        try {
            techreadClientHttps.uploadAssociatedFile(initResponse.getDrawing_presigned_post(), drawingBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the reading process and consume messages until the end
        sendReadCommand();
        Map<String, Object> results = new HashMap<String, Object>();
        while (this.techreadClientWss.isOpen()) {
            W24TechreadMessage message = techreadClientWss.waitForMessage();
            if (message == null) {
                break;
            }
            if (message.getMessage_type() == W24TechreadMessageType.ASK){

                results.put(
                    message.getMessage_subtype(),
                    message.getPayload_dict()
                );
            }

        }

        // Close HTTPS and WebSocket connections
        try {
            this.techreadClientHttps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.techreadClientWss.isOpen()) {
            this.techreadClientWss.close();
        }
        return results;
    }

    /**
     * Sends an initialization command.
     *
     * @param asks The list of queries.
     * @param maxPages The maximum number of pages to read.
     * @param drawingFilename The filename of the drawing.
     * @return The initialization response.
     */
    private W24TechreadInitResponse sendInitCommand(
        List<W24Ask> asks,
        int maxPages,
        String drawingFilename
    ) {
        // Get client version
        String client_version = getClientVersion();

        // Prepare techread request and send initialization command
        W24TechreadRequest request = new W24TechreadRequest(
            asks, null, client_version, maxPages, drawingFilename);
        try {
            W24TechreadMessage response = techreadClientWss.sendCommand(
                W24TechreadAction.INITIALIZE,
                objectMapper.convertValue(request, Map.class)
            );
            return objectMapper.convertValue(response.getPayload_dict(), W24TechreadInitResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the client version.
     */
    private String getClientVersion() {
        // Get client version
        String client_version = getClass().getPackage().getImplementationVersion();
        if (client_version == null) {
            client_version = "0.0.0";
        }
        return client_version + "-java";
    }

    /**
     * Sends a read command.
     */
    private void sendReadCommand() {
        // Send read command
        try {
            techreadClientWss.sendCommand(W24TechreadAction.READ, new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
