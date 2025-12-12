package es.onebox.flc.datasources.msvenue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8446926681033836904L;

    private Long id;
    private String name;
    private String address;
    private String postalCode;
    private IdNameCodeDTO country;
    private IdNameCodeDTO countrySubdivision;
    private List<IdNameDTO> spaces;
    @JsonProperty("public")
    private Boolean isPublic;
    private Long capacity;
    private String city;
    private VenueStatus status;
    private String pathLogo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public IdNameCodeDTO getCountry() {
        return country;
    }

    public void setCountry(IdNameCodeDTO country) {
        this.country = country;
    }

    public IdNameCodeDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(IdNameCodeDTO countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public List<IdNameDTO> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<IdNameDTO> spaces) {
        this.spaces = spaces;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public VenueStatus getStatus() {
        return status;
    }

    public void setStatus(VenueStatus status) {
        this.status = status;
    }

    public String getPathLogo() {
        return pathLogo;
    }

    public void setPathLogo(String pathLogo) {
        this.pathLogo = pathLogo;
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
