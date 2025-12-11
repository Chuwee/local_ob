package es.onebox.event.catalog.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author ignasi
 */
public class Venue implements Serializable {


    private static final long serialVersionUID = 5465015701061885327L;

    private Long id;
    private String name;
    private Long entityId;
    private String entityName;
    private String address;
    private String municipality;
    private String province;
    private String provinceCode;
    private String country;
    private String countryCode;
    private String postalCode;
    private String coordenates;
    private String timeZone;
    private String ownerCompany;
    private String managementCompany;
    private String contactRole;
    private String contactName;
    private String contactSurname;
    private String contactPhone;
    private String contactMail;
    private String image;
    private String googlePlaceId;

    public Venue() {

    }

    private Venue(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.entityId = builder.entityId;
        this.entityName = builder.entityName;
        this.address = builder.address;
        this.municipality = builder.municipality;
        this.province = builder.province;
        this.provinceCode = builder.provinceCode;
        this.country = builder.country;
        this.countryCode = builder.countryCode;
        this.postalCode = builder.postalCode;
        this.coordenates = builder.coordenates;
        this.timeZone = builder.timeZone;
        this.ownerCompany = builder.ownerCompany;
        this.managementCompany = builder.managementCompany;
        this.contactRole = builder.contactRole;
        this.contactName = builder.contactName;
        this.contactSurname = builder.contactSurname;
        this.contactPhone = builder.contactPhone;
        this.contactMail = builder.contactMail;
        this.image = builder.image;
        this.googlePlaceId = builder.googlePlaceId;
    }

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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCoordenates() {
        return coordenates;
    }

    public void setCoordenates(String coordenates) {
        this.coordenates = coordenates;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getManagementCompany() {
        return managementCompany;
    }

    public void setManagementCompany(String managementCompany) {
        this.managementCompany = managementCompany;
    }

    public String getContactRole() {
        return contactRole;
    }

    public void setContactRole(String contactRole) {
        this.contactRole = contactRole;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactSurname() {
        return contactSurname;
    }

    public void setContactSurname(String contactSurname) {
        this.contactSurname = contactSurname;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGooglePlaceId() { return googlePlaceId; }

    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String name;
        private Long entityId;
        private String entityName;
        private String address;
        private String municipality;
        private String province;
        private String provinceCode;
        private String country;
        private String countryCode;
        private String postalCode;
        private String coordenates;
        private String timeZone;
        private String ownerCompany;
        private String managementCompany;
        private String contactRole;
        private String contactName;
        private String contactSurname;
        private String contactPhone;
        private String contactMail;
        private String image;
        private String googlePlaceId;

        private Builder() {
            super();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEntityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder withEntityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder withMunicipality(String municipality) {
            this.municipality = municipality;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withProvince(String province) {
            this.province = province;
            return this;
        }

        public Builder withProvinceCode(String provinceCode) {
            this.provinceCode = provinceCode;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withCountryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public Builder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder withCoordenates(String coordenates) {
            this.coordenates = coordenates;
            return this;
        }

        public Builder withTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder withOwnerCompany(String ownerCompany) {
            this.ownerCompany = ownerCompany;
            return this;
        }

        public Builder withManagementCompany(String managementCompany) {
            this.managementCompany = managementCompany;
            return this;
        }

        public Builder withContactRole(String contactRole) {
            this.contactRole = contactRole;
            return this;
        }

        public Builder withContactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public Builder withContactSurname(String contactSurname) {
            this.contactSurname = contactSurname;
            return this;
        }

        public Builder withContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Builder withContactMail(String contactMail) {
            this.contactMail = contactMail;
            return this;
        }

        public Builder withImage(String image) {
            this.image = image;
            return this;
        }

        public Builder withGooglePlaceId(String googlePlaceId) {
            this.googlePlaceId = googlePlaceId;
            return this;
        }

        public Venue build() {
            return new Venue(this);
        }
    }
}
