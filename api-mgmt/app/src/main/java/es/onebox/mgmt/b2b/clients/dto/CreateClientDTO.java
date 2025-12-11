package es.onebox.mgmt.b2b.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;


public class CreateClientDTO extends BaseClientRequestDTO {

    private static final long serialVersionUID = 1L;

    @Length(max = 45, message = "iata_code max size 45")
    @JsonProperty("iata_code")
    private String iataCode;

    @Valid
    @NotNull(message = "user can not be null")
    private CreateClientUserDTO user;

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public CreateClientUserDTO getUser() {
        return user;
    }

    public void setUser(CreateClientUserDTO user) {
        this.user = user;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
