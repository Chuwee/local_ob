package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ClientB2B implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer clientId;
    private String businessName;
    private String description;
    private Integer clientCategoryId;
    private String iataCode;
    private String taxId;
    private String tags;
    private List<ClientB2BBranch> clientB2BBranches;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getClientCategoryId() {
        return clientCategoryId;
    }

    public void setClientCategoryId(Integer clientCategoryId) {
        this.clientCategoryId = clientCategoryId;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<ClientB2BBranch> getClientB2BBranches() {
        return clientB2BBranches;
    }

    public void setClientB2BBranches(List<ClientB2BBranch> clientB2BBranches) {
        this.clientB2BBranches = clientB2BBranches;
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
