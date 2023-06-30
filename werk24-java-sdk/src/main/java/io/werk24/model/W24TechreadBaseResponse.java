package io.werk24.model;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data public class W24TechreadBaseResponse {

    List<W24TechreadException> exceptions = new ArrayList<>();

    public boolean isSuccessful() {
        return this.exceptions.isEmpty();
    }
}
