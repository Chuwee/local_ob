package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

/**
 * @author mnavarro.
 */
public class ClientDTO implements Serializable {

    private Long id;

    private Long clientEntityId;

    private Integer typeId;

    private String name;

    private String address;

    private String country;

    private String countrySubdivision;

    private ClientB2BDTO clientB2B;

    public ClientUserDTO user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientEntityId() {
        return clientEntityId;
    }

    public void setClientEntityId(Long clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public ClientB2BDTO getClientB2B() {
        return clientB2B;
    }

    public void setClientB2B(ClientB2BDTO clientB2B) {
        this.clientB2B = clientB2B;
    }

    public ClientUserDTO getUser() {
        return user;
    }

    public void setUser(ClientUserDTO user) {
        this.user = user;
    }
}
