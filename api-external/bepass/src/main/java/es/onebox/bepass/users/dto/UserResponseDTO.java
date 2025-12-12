package es.onebox.bepass.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record UserResponseDTO(String id, @JsonProperty("callback_url") String onboardingUrl) implements Serializable {

    private static final long serialVersionUID = 1L;

}
