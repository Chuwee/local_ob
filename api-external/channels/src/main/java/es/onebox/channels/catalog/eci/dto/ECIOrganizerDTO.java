package es.onebox.channels.catalog.eci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ECIOrganizerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private ECIAddressDTO address;
    @JsonProperty("fiscal_identifier")
    private String fiscalIdentifier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ECIAddressDTO getAddress() {
        return address;
    }

    public void setAddress(ECIAddressDTO address) {
        this.address = address;
    }

    public String getFiscalIdentifier() {
        return fiscalIdentifier;
    }

    public void setFiscalIdentifier(String fiscalIdentifier) {
        this.fiscalIdentifier = fiscalIdentifier;
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
