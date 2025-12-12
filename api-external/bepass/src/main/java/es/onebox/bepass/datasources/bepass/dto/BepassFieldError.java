package es.onebox.bepass.datasources.bepass.dto;

import java.io.Serializable;
import java.util.List;

public record BepassFieldError(String field, List<String> messages) implements Serializable {

    private static final long serialVersionUID = 1L;

}
