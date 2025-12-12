package es.onebox.common.entities.dto;

import es.onebox.common.datasources.ms.entity.dto.EntityAccommodationsConfig;
import es.onebox.common.datasources.ms.entity.dto.EntityCustomization;
import es.onebox.common.datasources.ms.entity.dto.EntityInteractiveVenue;
import es.onebox.common.datasources.ms.entity.dto.EntityNotifications;
import es.onebox.common.datasources.ms.entity.dto.EntityStreaming;
import es.onebox.common.datasources.ms.entity.dto.WhatsappConfig;
import es.onebox.common.entities.enums.EntityStatus;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Entity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1339896481643086274L;
    private Long id;
    private String name;
    private String shortName;
    private String reference;
    private String nif;
    private String socialReason;
    private String notes;
    private IdValueCodeDTO language;
    private List<IdValueCodeDTO> selectedLanguages;
    private IdValueDTO timezone;
    private IdValueDTO currency;
    private String address;
    private String city;
    private Integer countryId;
    private Integer countrySubdivisionId;
    private String postalCode;
    private String email;
    private String phone;
    private String invoiceAddress;
    private String invoiceCity;
    private Integer invoiceCountryId;
    private Integer invoiceCountrySubdivisionId;
    private String invoicePostalCode;
    private String bankAccount;
    private Entity operator;
    private EntityStatus state;
    private List<EntityTypes> types;
    private Boolean useMultieventCart;
    private Boolean allowMultiAvetCart;
    private Boolean useActivityEvent;
    private Boolean useExternalAvetIntegration;
    private Boolean moduleB2BEnabled;
    private Boolean allowB2BPublishing;
    private Boolean allowInvitations;
    private Boolean useSecondaryMarket;
    private Integer maxUsers;
    private Integer avetClubCode;
    private EntityStreaming streaming;
    private EntityInteractiveVenue interactiveVenue;
    private EntityNotifications notifications;
    private EntityCustomization customization;
    private Boolean allowVipViews;
    private Boolean useCustomCategories;
    private List<IdDTO> selectedCategories;
    private String corporateColor;
    private Boolean allowMembers;
    private Boolean allowDataProtectionFields;
    private Boolean allowExternalManagement;
    private Boolean allowDigitalSeasonTicket;
    private Boolean allowTicketHidePrice;
    private Boolean allowExternalInvoiceNotification;
    private Boolean allowMassiveEmail;
    private Boolean allowFeverZone;

    private String shard;

    private Long clubCode;

    private Integer basicBIUsersLimit;
    private Integer advancedBIUsersLimit;
    private List<IdNameDTO> managedEntities;
    private EntityAccommodationsConfig accommodationsConfig;
    private WhatsappConfig whatsappConfig;
    private Boolean enableV4Configs;
    private String externalReference;

    public Entity() {
    }

    public Entity(Long id) {
        this.id = id;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public EntityStatus getState() {
        return state;
    }

    public void setState(EntityStatus state) {
        this.state = state;
    }

    public List<EntityTypes> getTypes() {
        return types;
    }

    public void setTypes(List<EntityTypes> types) {
        this.types = types;
    }

    public Boolean getUseMultieventCart() {
        return useMultieventCart;
    }

    public void setUseMultieventCart(Boolean useMultieventCart) {
        this.useMultieventCart = useMultieventCart;
    }

    public Boolean getAllowMultiAvetCart() {
        return allowMultiAvetCart;
    }

    public void setAllowMultiAvetCart(Boolean allowMultiAvetCart) {
        this.allowMultiAvetCart = allowMultiAvetCart;
    }

    public Boolean getUseActivityEvent() {
        return useActivityEvent;
    }

    public void setUseActivityEvent(Boolean useActivityEvent) {
        this.useActivityEvent = useActivityEvent;
    }

    public IdValueDTO getTimezone() {
        return timezone;
    }

    public void setTimezone(IdValueDTO timezone) {
        this.timezone = timezone;
    }

    public IdValueDTO getCurrency() {
        return currency;
    }

    public void setCurrency(IdValueDTO currency) {
        this.currency = currency;
    }

    public Entity getOperator() {
        return operator;
    }

    public void setOperator(Entity operator) {
        this.operator = operator;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCountrySubdivisionId() {
        return countrySubdivisionId;
    }

    public void setCountrySubdivisionId(Integer countrySubdivisionId) {
        this.countrySubdivisionId = countrySubdivisionId;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceCity(String invoiceCity) {
        this.invoiceCity = invoiceCity;
    }

    public String getInvoiceCity() {
        return invoiceCity;
    }

    public Integer getInvoiceCountryId() {
        return invoiceCountryId;
    }

    public void setInvoiceCountryId(Integer invoiceCountryId) {
        this.invoiceCountryId = invoiceCountryId;
    }

    public Integer getInvoiceCountrySubdivisionId() {
        return invoiceCountrySubdivisionId;
    }

    public void setInvoiceCountrySubdivisionId(Integer invoiceCountrySubdivisionId) {
        this.invoiceCountrySubdivisionId = invoiceCountrySubdivisionId;
    }

    public void setInvoicePostalCode(String invoicePostalCode) {
        this.invoicePostalCode = invoicePostalCode;
    }

    public String getInvoicePostalCode() {
        return invoicePostalCode;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public Integer getAvetClubCode() {
        return avetClubCode;
    }

    public void setAvetClubCode(Integer avetClubCode) {
        this.avetClubCode = avetClubCode;
    }

    public EntityStreaming getStreaming() {
        return streaming;
    }

    public void setStreaming(EntityStreaming streaming) {
        this.streaming = streaming;
    }

    public EntityInteractiveVenue getInteractiveVenue() {
        return interactiveVenue;
    }

    public void setInteractiveVenue(EntityInteractiveVenue interactiveVenue) {
        this.interactiveVenue = interactiveVenue;
    }

    public EntityNotifications getNotifications() {
        return notifications;
    }

    public void setNotifications(EntityNotifications notifications) {
        this.notifications = notifications;
    }

    public EntityCustomization getCustomization() {
        return customization;
    }

    public void setCustomization(EntityCustomization customization) {
        this.customization = customization;
    }

    public Boolean getUseCustomCategories() {
        return useCustomCategories;
    }

    public void setUseCustomCategories(Boolean useCustomCategories) {
        this.useCustomCategories = useCustomCategories;
    }

    public List<IdDTO> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<IdDTO> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public Boolean getUseExternalAvetIntegration() {
        return useExternalAvetIntegration;
    }

    public void setUseExternalAvetIntegration(Boolean useExternalAvetIntegration) {
        this.useExternalAvetIntegration = useExternalAvetIntegration;
    }

    public String getCorporateColor() {
        return corporateColor;
    }

    public void setCorporateColor(String corporateColor) {
        this.corporateColor = corporateColor;
    }

    public Boolean getAllowMembers() {
        return allowMembers;
    }

    public void setAllowMembers(Boolean allowMembers) {
        this.allowMembers = allowMembers;
    }

    public IdValueCodeDTO getLanguage() {
        return language;
    }

    public void setLanguage(IdValueCodeDTO language) {
        this.language = language;
    }

    public List<IdValueCodeDTO> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(List<IdValueCodeDTO> selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }

    public Boolean getModuleB2BEnabled() {
        return moduleB2BEnabled;
    }

    public void setModuleB2BEnabled(Boolean moduleB2BEnabled) {
        this.moduleB2BEnabled = moduleB2BEnabled;
    }

    public Boolean getAllowB2BPublishing() { return allowB2BPublishing; }

    public void setAllowB2BPublishing(Boolean allowB2BPublishing) { this.allowB2BPublishing = allowB2BPublishing; }

    public Boolean getAllowInvitations() { return allowInvitations; }

    public void setAllowInvitations(Boolean allowInvitations) {
        this.allowInvitations = allowInvitations;
    }

    public Boolean getUseSecondaryMarket() {
        return useSecondaryMarket;
    }

    public void setUseSecondaryMarket(Boolean useSecondaryMarket) {
        this.useSecondaryMarket = useSecondaryMarket;
    }

    public Boolean getAllowVipViews() {
        return allowVipViews;
    }

    public void setAllowVipViews(Boolean allowVipViews) {
        this.allowVipViews = allowVipViews;
    }

    public Boolean getAllowDataProtectionFields() {
        return allowDataProtectionFields;
    }

    public void setAllowDataProtectionFields(Boolean allowDataProtectionFields) {
        this.allowDataProtectionFields = allowDataProtectionFields;
    }

    public Boolean getAllowExternalManagement() {
        return allowExternalManagement;
    }

    public void setAllowExternalManagement(Boolean allowExternalManagement) {
        this.allowExternalManagement = allowExternalManagement;
    }

    public Boolean getAllowDigitalSeasonTicket() {
        return allowDigitalSeasonTicket;
    }

    public void setAllowDigitalSeasonTicket(Boolean allowDigitalSeasonTicket) {
        this.allowDigitalSeasonTicket = allowDigitalSeasonTicket;
    }

    public Boolean getAllowTicketHidePrice() {
        return allowTicketHidePrice;
    }

    public void setAllowTicketHidePrice(Boolean allowTicketHidePrice) {
        this.allowTicketHidePrice = allowTicketHidePrice;
    }

    public Boolean getAllowFeverZone() {
        return allowFeverZone;
    }

    public void setAllowFeverZone(Boolean allowFeverZone) {
        this.allowFeverZone = allowFeverZone;
    }

    public Boolean getAllowMassiveEmail() {
        return allowMassiveEmail;
    }

    public void setAllowMassiveEmail(Boolean allowMassiveEmail) {
        this.allowMassiveEmail = allowMassiveEmail;
    }

    public String getShard() {
        return shard;
    }

    public void setShard(String shard) {
        this.shard = shard;
    }

    public Long getClubCode() {
        return clubCode;
    }

    public void setClubCode(Long clubCode) {
        this.clubCode = clubCode;
    }

    public Integer getBasicBIUsersLimit() {
        return basicBIUsersLimit;
    }

    public void setBasicBIUsersLimit(Integer basicBIUsersLimit) {
        this.basicBIUsersLimit = basicBIUsersLimit;
    }

    public Integer getAdvancedBIUsersLimit() {
        return advancedBIUsersLimit;
    }

    public void setAdvancedBIUsersLimit(Integer advancedBIUsersLimit) {
        this.advancedBIUsersLimit = advancedBIUsersLimit;
    }

    public Boolean getAllowExternalInvoiceNotification() {
        return allowExternalInvoiceNotification;
    }

    public void setAllowExternalInvoiceNotification(Boolean allowExternalInvoiceNotification) {
        this.allowExternalInvoiceNotification = allowExternalInvoiceNotification;
    }

    public List<IdNameDTO> getManagedEntities() {
        return managedEntities;
    }

    public void setManagedEntities(List<IdNameDTO> managedEntities) {
        this.managedEntities = managedEntities;
    }


    public Boolean getEnableV4Configs() { return enableV4Configs; }

    public void setEnableV4Configs(Boolean enableV4Configs) { this.enableV4Configs = enableV4Configs; }

    public EntityAccommodationsConfig getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(EntityAccommodationsConfig accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    public WhatsappConfig getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfig whatsappConfig) {
        this.whatsappConfig = whatsappConfig;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
