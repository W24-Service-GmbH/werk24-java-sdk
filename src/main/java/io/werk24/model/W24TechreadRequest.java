package io.werk24.model;
import java.util.List;

import lombok.Data;

@Data
public class W24TechreadRequest {
    /**
     *  Definition of a W24DrawingReadRequest containing
     * all the asks (i.e., things you want to learn about
     * the technical drawing).
     */

    /**
     * List of asks that shall be performed by the server.
     */
    private final List<W24Ask> asks;

    /**
     * The development_key is used for internal purposes.
     * It wil give you access to pre-release versions of our software.
     * Interested to get access? Apply today. Always hiring.
     */
    private final String development_key;

    /**
     * Current version of the client.
     */
    private final String client_version;

    /**
     * Maximum number of pages that shall be processed.
     */
    private final int max_pages;

    /**
     * Optional filename
     */
    private final String drawing_filename;
}
