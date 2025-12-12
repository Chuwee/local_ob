package es.onebox.bepass.datasources.bepass.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.List;

public record BepassError(String statusCode,
                          @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                          List<String> message, String status, List<BepassFieldError> errors) implements Serializable {

    private static final long serialVersionUID = 1L;

}
