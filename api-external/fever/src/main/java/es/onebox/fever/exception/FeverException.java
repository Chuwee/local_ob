package es.onebox.fever.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.exception.OneboxRestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeverException extends OneboxRestException {

    private final String detail;
    private final DetailError error;
    private final Map<String, List<String>> headers;

    @JsonCreator
    public FeverException(
            @JsonProperty("detail") String detail,
            @JsonProperty("error") DetailError error
                         ) {
        this.detail = detail;
        this.error = error;
        headers = new HashMap<>();
    }

    public String getDetail() {
        return detail;
    }

    public DetailError getError() {
        return error;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public record DetailError(
            String code,
            String message) {
    }
}
