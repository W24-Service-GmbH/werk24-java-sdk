package io.werk24.model;

enum W24TechreadMessageSubtype {
    /**
     * Message Subtype
     */
    // ERROR
    UNSUPPORTED_DRAWING_FILE_FORMAT,
    INTERNAL,
    TIMEOUT,

    // REJECTION
    COMPLEXITY_EXCEEDED,
    PAPER_SIZE_LIMIT_EXCEEDED,

    // PROGRESS
    INITIALIZATION_SUCCESS,
    COMPLETED,
    STARTED;

}
