package io.werk24.model;
import lombok.Data;
import java.util.Map;

@Data
public class W24TechreadCommand {
    private final W24TechreadAction action;
    private final Map message;
}
