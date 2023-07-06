package io.werk24.model;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data public class W24TechreadBaseResponse {
    /**
     * BaseFormat for messages returned by the server
     */

    /**
     * List of exceptions that occurred during the processing.
     */
    List<W24TechreadException> exceptions = new ArrayList<>();

    /**
     * @return true if no exceptions occurred during the processing.
     */
    public boolean isSuccessful() {
        return this.exceptions.isEmpty();
    }
}
