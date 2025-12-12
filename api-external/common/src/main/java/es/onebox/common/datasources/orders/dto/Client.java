package es.onebox.common.datasources.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Client implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long clientEntityId;
    private Integer typeId;
    private String name;
    private String address;
    private String country;
    private String countrySubdivision;
    private ClientUser user;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientEntityId() {
        return this.clientEntityId;
    }

    public void setClientEntityId(Long clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrySubdivision() {
        return this.countrySubdivision;
    }

    public void setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public ClientUser getUser() {
        return this.user;
    }

    public void setUser(ClientUser user) {
        this.user = user;
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
