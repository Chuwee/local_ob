package es.onebox.common.datasources.ms.client.dto;

import java.io.Serializable;

public class Client implements Serializable {

    private Integer id;
    private Integer typeId;
    private String name;
    private String address;
    private String country;
    private String countrySubdivision;
    private ClientB2B clientB2B;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ClientB2B getClientB2B() {
        return clientB2B;
    }

    public void setClientB2B(ClientB2B clientB2B) {
        this.clientB2B = clientB2B;
    }
}