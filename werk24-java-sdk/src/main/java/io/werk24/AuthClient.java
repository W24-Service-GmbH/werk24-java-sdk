package io.werk24;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdRequest;

public class AuthClient {
    private String cognitoRegion;
    private String cognitoIdentityPoolId;
    private String cognitoUserPoolId;
    private String cognitoClientId;
    private String cognitoClientSecret;
    private String username;
    private String password;
    private String token;
    private Date expiresAt;

    /**
     * Initiate a new Authentication client
     *
     * @param   cognitoRegion AWS Region
     * @param   cognitoIdentityPoolId Id of the idenity pool
     * @param   cognitoUserPoolId Id of the user pool
     * @param   cognitoClientId Id of the Client
     * @param   cognitoClientSecret Secret of the Client
     */
    public AuthClient(
        String cognitoRegion,
        String cognitoIdentityPoolId,
        String cognitoUserPoolId,
        String cognitoClientId,
        String cognitoClientSecret
    ) {
        this.cognitoRegion = cognitoRegion;
        this.cognitoIdentityPoolId = cognitoIdentityPoolId;
        this.cognitoUserPoolId = cognitoUserPoolId;
        this.cognitoClientId = cognitoClientId;
        this.cognitoClientSecret = cognitoClientSecret;
        this.username = null;
        this.password = null;
        this.token = null;
        this.expiresAt = null;
    }

    /**
     * Buffer the username and password.
     *
     * @param   username Username of the user
     * @param   password Password of the user
     */
    public void register(
        String username,
        String password
    ) {
        this.username = username;
        this.password = password;
    }

    public String getToken() {
        if (this.tokenHasExpired()) {
            try {
                this.performLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.token;
    }

    /**
     * Make a Cognito Secret Hash.
     *
     * @param  username Username of the user
     * @throws Exception
     * @return String
     */
    private String makeCognitoSecretHash(String username) throws Exception {
        String message = username + this.cognitoClientId;
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(this.cognitoClientSecret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secretKey);

        byte[] hash = sha256_HMAC.doFinal(message.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Login.
     *
     * This can be called multiple times. It will only perform the login
     * if the token is not set or has expired.
     *
     * @throws Exception
     */
    public void login() throws Exception {
        if (this.token == null || this.tokenHasExpired()) {
            this.performLogin();
        }
    }

    /**
     * Check if the token has expired.
     *
     * Note: we want to give ourselves a 10 minute buffer.
     * This should be enough to process the request.
     *
     * @return boolean
     */
    private boolean tokenHasExpired() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, 10);
        return this.expiresAt.before(cal.getTime());
    }

    /**
     * Perform the actual login
     * @throws Exception
     */
    private void performLogin() throws Exception {
        if (this.username == null || this.password == null) {
            throw new Exception("No username / password provided");
        }

         // Create Cognito Identity Provider client
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        // Create the request object
        InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(Map.of("USERNAME", this.username, "PASSWORD", this.password, "SECRET_HASH", this.makeCognitoSecretHash(this.username)))
                .clientId(this.cognitoClientId)
                .build();


        // Authenticate the user and get the response
        InitiateAuthResponse response = cognitoClient.initiateAuth(initiateAuthRequest);

        // Get ID token
        this.token = response.authenticationResult().idToken();

        // Set the expiration time
        Integer expiresIn = response.authenticationResult().expiresIn();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, expiresIn);
        this.expiresAt = cal.getTime();

        // Close the Cognito client
        cognitoClient.close();

    }
}
