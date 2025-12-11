package es.onebox.mgmt.datasources.ms.venue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Venue implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private VenueStatus status;

    @JsonProperty("entity")
    private IdNameDTO entity;

    @JsonProperty("city")
    private String city;

    @JsonProperty("timezone")
    private TimeZone timezone;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("type")
    private VenueType type;

    @JsonProperty("isPublic")
    private Boolean isPublic;

    @JsonProperty("calendar")
    private IdNameDTO calendar;

    @JsonProperty("country")
    private IdNameCodeDTO country;

    @JsonProperty("countrySubdivision")
    private IdNameCodeDTO countrySubdivision;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @JsonProperty("manager")
    private String manager;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("website")
    private String website;

    @JsonProperty("contact")
    private VenueContact contact;

    @JsonProperty("pathLogo")
    private String pathLogo;

    @JsonProperty("spaces")
    private List<IdNameDTO> spaces;

    @JsonProperty("googlePlaceId")
    private String googlePlaceId;

    @JsonProperty("externalId")
    private Long externalId;

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

    public VenueStatus getStatus() {
        return status;
    }

    public void setStatus(VenueStatus status) {
        this.status = status;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public VenueType getType() {
        return type;
    }

    public void setType(VenueType type) {
        this.type = type;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public IdNameCodeDTO getCountry() {
        return country;
    }

    public void setCountry(IdNameCodeDTO country) {
        this.country = country;
    }

    public IdNameDTO getCalendar() {
        return calendar;
    }

    public void setCalendar(IdNameDTO calendar) {
        this.calendar = calendar;
    }

    public IdNameCodeDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(IdNameCodeDTO countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public VenueContact getContact() {
        return contact;
    }

    public void setContact(VenueContact contact) {
        this.contact = contact;
    }

    public String getPathLogo() {
        return pathLogo;
    }

    public void setPathLogo(String pathLogo) {
        this.pathLogo = pathLogo;
    }

    public List<IdNameDTO> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<IdNameDTO> spaces) {
        this.spaces = spaces;
    }

    public String getGooglePlaceId() { return googlePlaceId; }

    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }

    public Long getExternalId() { return externalId; }

    public void setExternalId(Long externalId) { this.externalId = externalId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
