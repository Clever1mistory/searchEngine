package searchengine.dto.responses;

import lombok.Value;

@Value
public class Error {
    boolean result;
    String errorMessage;
}
