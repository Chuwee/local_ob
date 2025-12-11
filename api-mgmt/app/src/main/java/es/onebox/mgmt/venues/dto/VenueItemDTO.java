package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class VenueItemDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("entity")
    private IdNameDTO entity;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private CodeNameDTO country;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("type")
    private VenueType type;

    @JsonProperty("public")
    private Boolean isPublic;

    @JsonProperty("calendar")
    private IdNameDTO calendar;

    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("coordinates")
    private CoordinatesDTO coordinates;

    @JsonProperty("manager")
    private String manager;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("website")
    private String website;

    @JsonProperty("image_logo_url")
    private String imageLogoUrl;

    @JsonProperty("contact")
    private VenueContactDTO contact;

    @JsonProperty("google_place_id")
    public String googlePlaceId;

    @JsonProperty("external_id")
    public Long externalId;

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

    public CodeNameDTO getCountry() {
        return country;
    }

    public void setCountry(CodeNameDTO country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
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

    public IdNameDTO getCalendar() {
        return calendar;
    }

    public void setCalendar(IdNameDTO calendar) {
        this.calendar = calendar;
    }

    public CodeNameDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(CodeNameDTO countrySubdivision) {
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

    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDTO coordinates) {
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

    public String getImageLogoUrl() {
        return imageLogoUrl;
    }

    public void setImageLogoUrl(String imageLogoUrl) {
        this.imageLogoUrl = imageLogoUrl;
    }

    public VenueContactDTO getContact() {
        return contact;
    }

    public void setContact(VenueContactDTO contact) {
        this.contact = contact;
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
