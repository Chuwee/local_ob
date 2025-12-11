package es.onebox.event.catalog.elasticsearch.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Entity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5490465058044088185L;

    private Integer id;
    private String name;
    private String corporateName;
    private Integer status;
    private Boolean usesExternalManagement;
    private String fiscalCode;
    private String address;
    private String city;
    private String postalCode;
    private Integer countryId;
    private String countryName;
    private String countryCode;
    private Integer countrySubdivisionId;
    private String countrySubdivisionName;
    private String countrySubdivisionCode;
    private Integer operatorId;
    private Integer operatorStatus;
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getUsesExternalManagement() {
        return usesExternalManagement;
    }

    public void setUsesExternalManagement(Boolean usesExternalManagement) {
        this.usesExternalManagement = usesExternalManagement;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getCountrySubdivisionId() {
        return countrySubdivisionId;
    }

    public void setCountrySubdivisionId(Integer countrySubdivisionId) {
        this.countrySubdivisionId = countrySubdivisionId;
    }

    public String getCountrySubdivisionName() {
        return countrySubdivisionName;
    }

    public void setCountrySubdivisionName(String countrySubdivisionName) {
        this.countrySubdivisionName = countrySubdivisionName;
    }

    public String getCountrySubdivisionCode() {
        return countrySubdivisionCode;
    }

    public void setCountrySubdivisionCode(String countrySubdivisionCode) {
        this.countrySubdivisionCode = countrySubdivisionCode;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOperatorStatus() {
        return operatorStatus;
    }

    public void setOperatorStatus(Integer operatorStatus) {
        this.operatorStatus = operatorStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
