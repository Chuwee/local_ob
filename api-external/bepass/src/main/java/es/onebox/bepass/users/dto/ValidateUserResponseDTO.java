package es.onebox.bepass.users.dto;

import java.io.Serializable;

public record ValidateUserResponseDTO(String token) implements Serializable {

    private static final long serialVersionUID = 1L;
}
