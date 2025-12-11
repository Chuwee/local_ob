package es.onebox.event.events.dto;

import es.onebox.event.events.enums.EventAvetConfigType;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class CreateEventRequestDTO {

    private String name;
    private String promoterReference;
    private EventType type;
    private Long entityId;
    private Long producerId;
    private Integer categoryId;
    private Long currencyId;

    private Integer avetCompetitionId;
    private EventAvetConfigType avetConfig;
    private Integer defaultLangId;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private List<Long> entityFavoriteChannels;
    private Integer invoicePrefixId;
    private Provider inventoryProvider;


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

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonSurname() {
        return contactPersonSurname;
    }

    public void setContactPersonSurname(String contactPersonSurname) {
        this.contactPersonSurname = contactPersonSurname;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Integer getAvetCompetitionId() {
        return avetCompetitionId;
    }

    public void setAvetCompetitionId(Integer avetCompetitionId) {
        this.avetCompetitionId = avetCompetitionId;
    }

    public EventAvetConfigType getAvetConfig() {
        return avetConfig;
    }

    public void setAvetConfig(EventAvetConfigType avetConfig) {
        this.avetConfig = avetConfig;
    }

    public List<Long> getEntityFavoriteChannels() {
        return entityFavoriteChannels;
    }

    public void setEntityFavoriteChannels(List<Long> entityFavoriteChannels) {
        this.entityFavoriteChannels = entityFavoriteChannels;
    }

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }
    
    public Provider getInventoryProvider() {
        return inventoryProvider;
    }
    
    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
