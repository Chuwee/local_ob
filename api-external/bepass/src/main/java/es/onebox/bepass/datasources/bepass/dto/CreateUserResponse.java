package es.onebox.bepass.datasources.bepass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record CreateUserResponse(String sup, String status, @JsonProperty("onboarding_url") String onboardingUrl,
                                 Long timestamp) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
