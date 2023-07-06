package io.werk24.model;
import lombok.Data;

@Data public class W24Ask {
    /**
     * Base model from which all Asks inherit.
     *
     * When you send a request with the attribute is_training set to true,
     * you are directly improving the quality of our Machine Learning Models with regards
     * to your domain. These requests are not charged, but they also do not generate a response.
     * The connection is immediately closed after you submit the request; our system then processes
     * the drawing when the system load is low.
     */

    /**
     * Type of the requested Ask. Used for de-serialization.
     */
    private final String ask_type;

    /**
     * Flag that indicates that your request is a pure training request
     * and that you are not expecting to obtain a response.
     */
    private final boolean is_training = false;
}
