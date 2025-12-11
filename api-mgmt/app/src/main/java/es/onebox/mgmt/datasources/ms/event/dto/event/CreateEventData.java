package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.datasources.common.dto.ContactData;
import es.onebox.mgmt.events.enums.EventAvetConfigType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

public class CreateEventData extends ContactData {

    private String name;
    private String promoterReference;
    private EventType type;
    private Long entityId;
    private Long producerId;
    private Integer categoryId;
    private Long currencyId;
    private Integer defaultLangId;
    private List<Long> entityFavoriteChannels;
    private EventAvetConfigType avetConfig;
    private Integer avetCompetitionId;
    private Long invoicePrefixId;
    private Map<String, Object> additionalConfig;
    private Provider inventoryProvider;

    public CreateEventData() {
    }

    public CreateEventData(String name, String promoterReference, EventType type, Long entityId, Long producerId, Long invoicePrefixId,
                           Integer categoryId, Integer defaultLangId, List<Long> entityFavoriteChannels, Integer avetCompetitionId,
                           EventAvetConfigType avetConfig, Long currencyId, Map<String, Object> additionalConfig) {
        this.name = name;
        this.promoterReference = promoterReference;
        this.type = type;
        this.entityId = entityId;
        this.producerId = producerId;
        this.invoicePrefixId = invoicePrefixId;
        this.categoryId = categoryId;
        this.defaultLangId = defaultLangId;
        this.entityFavoriteChannels = entityFavoriteChannels;
        this.avetCompetitionId = avetCompetitionId;
        this.avetConfig = avetConfig;
        this.currencyId = currencyId;
        this.additionalConfig = additionalConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPromoterReference() {
        return promoterReference;
    }

    public void setPromoterReference(String promoterReference) {
        this.promoterReference = promoterReference;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
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

    public Integer getDefaultLangId() {
        return defaultLangId;
    }

    public void setDefaultLangId(Integer defaultLangId) {
        this.defaultLangId = defaultLangId;
    }

    public List<Long> getEntityFavoriteChannels() {
        return entityFavoriteChannels;
    }

    public void setEntityFavoriteChannels(List<Long> entityFavoriteChannels) {
        this.entityFavoriteChannels = entityFavoriteChannels;
    }

    public EventAvetConfigType getAvetConfig() {
        return avetConfig;
    }

    public void setAvetConfig(EventAvetConfigType avetConfig) {
        this.avetConfig = avetConfig;
    }

    public Integer getAvetCompetitionId() {
        return avetCompetitionId;
    }

    public void setAvetCompetitionId(Integer avetCompetitionId) {
        this.avetCompetitionId = avetCompetitionId;
    }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public Map<String, Object> getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(Map<String, Object> additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public Provider getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
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
