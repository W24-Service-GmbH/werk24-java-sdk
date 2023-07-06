package io.werk24.model;
import lombok.Data;

@Data public class W24TechreadException {
    /**
     * Error message that accompanies the W24TechreadMessage
     * if an error occurred.
     */

    /**
     * Error level indicating the severity of the error
     */
    W24TechreadExceptionLevel exception_level;

    /**
     * Error Type that allows the API-user to translate
     * the message to a user-info.
     */
    W24TechreadExceptionType exception_type;
}
