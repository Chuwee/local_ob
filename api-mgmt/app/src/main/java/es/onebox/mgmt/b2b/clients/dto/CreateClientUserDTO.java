package es.onebox.mgmt.b2b.clients.dto;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public class CreateClientUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Length(max = 200, message = "username max size 200")
    @NotNull(message = "username can not be null")
    private String username;

    @Length(max = 200, message = "name max size 200")
    @NotNull(message = "name can not be null")
    private String name;

    @Length(max = 200, message = "email max size 200")
    @NotNull(message = "email can not be null")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
