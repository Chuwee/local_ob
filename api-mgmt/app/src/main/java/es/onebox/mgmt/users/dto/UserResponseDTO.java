package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class UserResponseDTO extends UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("entity")
    private UserEntityDTO entity;

    public UserEntityDTO getEntity() {
        return entity;
    }

    public void setEntity(UserEntityDTO entity) {
        this.entity = entity;
    }
}


