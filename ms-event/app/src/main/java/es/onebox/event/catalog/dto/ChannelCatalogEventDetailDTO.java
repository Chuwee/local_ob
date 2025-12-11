package es.onebox.event.catalog.dto;

import es.onebox.event.attendants.dto.AttendantsConfig;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionDTO;
import es.onebox.event.events.dto.EventWhitelabelSettingsDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class ChannelCatalogEventDetailDTO extends ChannelCatalogEventDTO {

    @Serial
    private static final long serialVersionUID = -792227579814979866L;

    private List<CatalogPromotionDTO> promotions;
    private CatalogEntityDTO eventEntity;
    private CatalogEntityDTO promoterEntity;
    private Boolean usePromoterFiscalData;
    private CatalogContactInfo contact;
    private CatalogInvoicePrefixDTO invoicePrefix;
    private EventWhitelabelSettingsDTO eventWhitelabelSettings;
    private Map<String, String> infoBannerSaleRequest;
    private AttendantsConfig attendantsConfig;
    private List<EventAttendantFieldDTO> attendantFields;
    private Boolean mandatoryLogin;
    private Integer customerMaxSeats;
    private Boolean phoneValidationRequired;
    private Boolean attendantVerificationRequired;
    private ChangeSeatsConfig eventChangeSeatConfig;

    public List<CatalogPromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<CatalogPromotionDTO> promotions) {
        this.promotions = promotions;
    }

    public CatalogEntityDTO getEventEntity() {
        return eventEntity;
    }

    public void setEventEntity(CatalogEntityDTO eventEntity) {
        this.eventEntity = eventEntity;
    }

    public CatalogEntityDTO getPromoterEntity() {
        return promoterEntity;
    }

    public void setPromoterEntity(CatalogEntityDTO promoterEntity) {
        this.promoterEntity = promoterEntity;
    }

    public Boolean getUsePromoterFiscalData() {
        return usePromoterFiscalData;
    }

    public void setUsePromoterFiscalData(Boolean usePromoterFiscalData) {
        this.usePromoterFiscalData = usePromoterFiscalData;
    }

    public CatalogContactInfo getContact() {
        return contact;
    }

    public void setContact(CatalogContactInfo contact) {
        this.contact = contact;
    }

    public CatalogInvoicePrefixDTO getInvoicePrefix() {
        return invoicePrefix;
    }

    public void setInvoicePrefix(CatalogInvoicePrefixDTO invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
    }

    public EventWhitelabelSettingsDTO getEventWhitelabelSettings() {
        return eventWhitelabelSettings;
    }

    public void setEventWhitelabelSettings(EventWhitelabelSettingsDTO eventWhitelabelSettings) {
        this.eventWhitelabelSettings = eventWhitelabelSettings;
    }
    public Map<String, String> getInfoBannerSaleRequest() { return infoBannerSaleRequest; }

    public void setInfoBannerSaleRequest(Map<String, String> infoBannerSaleRequest) {
        this.infoBannerSaleRequest = infoBannerSaleRequest;
    }

    public AttendantsConfig getAttendantsConfig() {
        return attendantsConfig;
    }

    public void setAttendantsConfig(AttendantsConfig attendantsConfig) {
        this.attendantsConfig = attendantsConfig;
    }

    public List<EventAttendantFieldDTO> getAttendantFields() {
        return attendantFields;
    }

    public void setAttendantFields(List<EventAttendantFieldDTO> attendantFields) {
        this.attendantFields = attendantFields;
    }

    public Boolean getMandatoryLogin() { return mandatoryLogin; }

    public void setMandatoryLogin(Boolean mandatoryLogin) {
        this.mandatoryLogin = mandatoryLogin;
    }

    public Integer getCustomerMaxSeats() { return customerMaxSeats; }

    public void setCustomerMaxSeats(Integer customerMaxSeats) { this.customerMaxSeats = customerMaxSeats; }

    public Boolean getPhoneValidationRequired() {
        return phoneValidationRequired;
    }

    public void setPhoneValidationRequired(Boolean phoneValidationRequired) {
        this.phoneValidationRequired = phoneValidationRequired;
    }

    public Boolean getAttendantVerificationRequired() {
        return attendantVerificationRequired;
    }

    public void setAttendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
    }

    public ChangeSeatsConfig getEventChangeSeatConfig() {
        return eventChangeSeatConfig;
    }

    public void setEventChangeSeatConfig(ChangeSeatsConfig eventChangeSeatConfig) {
        this.eventChangeSeatConfig = eventChangeSeatConfig;
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
