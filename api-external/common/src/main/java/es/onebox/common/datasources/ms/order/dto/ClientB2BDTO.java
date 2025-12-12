package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

/**
 * @author mnavarro.
 */
public class ClientB2BDTO implements Serializable {

    public Long clientId;

    public String businessName;

    public String description;

    public Integer clientCategoryId;

    public String iataCode;

    public String taxId;

    public String tags;

    public ClientB2BBranchDTO clientB2BBranch;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
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

    public ClientB2BBranchDTO getClientB2BBranch() {
        return clientB2BBranch;
    }

    public void setClientB2BBranch(ClientB2BBranchDTO clientB2BBranch) {
        this.clientB2BBranch = clientB2BBranch;
    }
}
