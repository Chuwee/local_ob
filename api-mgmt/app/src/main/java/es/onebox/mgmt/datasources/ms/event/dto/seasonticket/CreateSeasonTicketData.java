package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.mgmt.datasources.common.dto.ContactData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

public class CreateSeasonTicketData extends ContactData {

    private String name;
    private Long entityId;
    private Long producerId;
    private Integer categoryId;
    private Integer customCategoryId;
    private Integer defaultLangId;
    private Long currencyId;
    private Long invoicePrefixId;
    private Map<String, Object> additionalConfig;

    public CreateSeasonTicketData(String name, Long entityId, Long producerId, Integer categoryId, Integer customCategoryId, Integer defaultLangId, Long currencyId,
                                  Long invoicePrefixId, Map<String, Object> additionalConfig) {
        this.name = name;
        this.entityId = entityId;
        this.producerId = producerId;
        this.categoryId = categoryId;
        this.customCategoryId = customCategoryId;
        this.defaultLangId = defaultLangId;
        this.currencyId = currencyId;
        this.invoicePrefixId = invoicePrefixId;
        this.additionalConfig = additionalConfig;
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

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }

    public Integer getDefaultLangId() {
        return defaultLangId;
    }

    public void setDefaultLangId(Integer defaultLangId) {
        this.defaultLangId = defaultLangId;
    }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public Map<String, Object> getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(Map<String, Object> additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public Integer getCustomCategoryId() {
        return customCategoryId;
    }

    public void setCustomCategoryId(Integer customCategoryId) {
        this.customCategoryId = customCategoryId;
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
