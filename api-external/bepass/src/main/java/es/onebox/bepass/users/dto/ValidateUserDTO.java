package es.onebox.bepass.users.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class ValidateUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "type is required")
    private ValidationMethod type;
    @NotNull(message = "value is required")
    private String id;

    public ValidationMethod getType() {
        return type;
    }

    public void setType(ValidationMethod type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
