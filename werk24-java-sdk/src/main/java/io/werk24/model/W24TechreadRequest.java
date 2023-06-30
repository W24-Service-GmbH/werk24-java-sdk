package io.werk24.model;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
public class W24TechreadRequest {
    private final List<W24Ask> asks;
    private final String development_key;
    private final String client_version; // TODO set client version.
    private final int max_pages;
    private final String drawing_filename;
}
