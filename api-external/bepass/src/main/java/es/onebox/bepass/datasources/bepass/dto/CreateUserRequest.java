package es.onebox.bepass.datasources.bepass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record CreateUserRequest(String externalId,
                                String callbackURL,
                                String postbackURL,
                                String firstName,
                                String lastName,
                                String document,
                                @JsonProperty("document_type") String documentType,
                                String gender,
                                String birthDate,
                                String phoneNumber,
                                String email,
                                String originCompanyId) implements Serializable  {
    @Serial
    private static final long serialVersionUID = 1L;
}
