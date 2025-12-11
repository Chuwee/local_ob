package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.validation.TimeZone;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class VenueItemRequestDTO {

    @JsonProperty("name")
    @Size(max=200, message = "venue names must be between 0 and 200 chars")
    private String name;

    @JsonProperty("timezone")
    @TimeZone
    private String timezone;

    @JsonProperty("capacity")
    @Positive
    private Integer capacity;

    @JsonProperty("type")
    private VenueType type;

    @JsonProperty("calendar_id")
    private Integer calendarId;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("country_subdivision_code")
    private String countrySubdivisionCode;

    @JsonProperty("city")
    @Size(max=50, message = "city names must be under 50 chars")
    private String city;

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

    @JsonProperty("contact")
    private VenueContactDTO contact;

    @JsonProperty("google_place_id")
    public String googlePlaceId;

    @JsonProperty("external_id")
    public Long externalId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
        this.calendarId = calendarId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountrySubdivisionCode() {
        return countrySubdivisionCode;
    }

    public void setCountrySubdivisionCode(String countrySubdivisionCode) {
        this.countrySubdivisionCode = countrySubdivisionCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
