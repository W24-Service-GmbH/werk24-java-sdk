package io.werk24.model;

public enum W24TechreadExceptionType {
    /**
     * List of all the error types that can possibly
     * be associated to the error type.
     */

    /**
     * The Drawing was submitted in a file format that is not
     * supported by the API at this stage.
     */
    DRAWING_FILE_FORMAT_UNSUPPORTED,

    /**
     *The Drawing file size exceeded the limit
     */
    DRAWING_FILE_SIZE_TOO_LARGE,

    /**
     * The resolution (dots per inch) was too low to be
     * processed
     */
    DRAWING_RESOLUTION_TOO_LOW,

    /**
     * The amount of noise on the drawing was too hight for us
     * to understand the drawing
     */
    DRAWING_NOISE_TOO_HIGH,

    /**
     * The file you submitted as drawing might not actually
     * be a drawing
     */
    DRAWING_CONTENT_NOT_UNDERSTOOD,

    /**
     * The paper size is larger that the allowed paper size
     */
    DRAWING_PAPER_SIZE_TOO_LARGE,

    /**
     * The file you submitted contains too few measures to be
     * manufacturable.
     */
    DRAWING_MEASURE_SYSTEM_INCOMPLETE,

    /**
     * The Model was submitted in a file format that is not supported
     * by the API at this stage.
     */
    MODEL_FILE_FORMAT_UNSUPPORTED,

    /**
     * The Model file size exceeded the limit
     */
    MODEL_FILE_SIZE_TOO_LARGE,

    /**
     * Raised when the sub_account does not belong to the
     * main account.
     */
    SUB_ACCOUNT_ACCESS_DENIED,

    /**
     * Raised when the sub account is pre-paid and the budget
     * is exhausted.
     */
    SUB_ACCOUNT_NO_BALANCE
}
