package es.onebox.eci.organizations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Organization implements Serializable {

    private static final long serialVersionUID = -3878240985822297788L;

    private String identifier;
    private String name;
    @JsonProperty("tax_id")
    private String taxId;
    @JsonProperty("organization_types")
    private Set<OrganizationType> organizationTypes;
    private List<Brand> brands;
    private Address address;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Set<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(Set<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Organization organization)) {
            return false;
        }

        return identifier.equals(organization.identifier)
                && taxId.equals(organization.taxId);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
