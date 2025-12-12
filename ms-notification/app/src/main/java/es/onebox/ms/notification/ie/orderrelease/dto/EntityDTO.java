package es.onebox.ms.notification.ie.orderrelease.dto;


import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.ms.notification.ie.orderrelease.enums.EntityState;

import java.io.Serializable;

public class EntityDTO implements Serializable {
    
    private Integer id;
    private EntityDTO operator;
    private String name;
    private String description;
    private EntityState state;
    private String socialReason;
    private String imagePath;
    private Boolean usingExternalManagement;
    private TimeZoneGroupDTO timeZoneGroup;
    private String documentId;
    private String address;
    private String city;
    private String postalCode;
    private String invoiceDocumentId;
    private String invoiceAddress;
    private String invoiceCity;
    private String invoicePostalCode;
    private String defaultLanguage;
    private Boolean allowInvoiceGeneration;
    private Boolean moduleB2BEnabled;
    private IdCodeDTO currency;
    private String shortName;
    private Boolean allowSecMKT;
    private Boolean useMultiCurrency;

    public EntityDTO() {
    }

    public EntityDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EntityDTO getOperator() {
        return operator;
    }

    public void setOperator(EntityDTO operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityState getState() {
        return state;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TimeZoneGroupDTO getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public void setTimeZoneGroup(TimeZoneGroupDTO timeZoneGroup) {
        this.timeZoneGroup = timeZoneGroup;
    }

    public void setUsingExternalManagement(Boolean usingExternalManagement) {
        this.usingExternalManagement = usingExternalManagement;
    }

    public Boolean getUsingExternalManagement() {
        return usingExternalManagement;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInvoiceCity() {
        return invoiceCity;
    }

    public void setInvoiceCity(String invoiceCity) {
        this.invoiceCity = invoiceCity;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getInvoicePostalCode() {
        return invoicePostalCode;
    }

    public void setInvoicePostalCode(String invoicePostalCode) {
        this.invoicePostalCode = invoicePostalCode;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getInvoiceDocumentId() {
        return invoiceDocumentId;
    }

    public void setInvoiceDocumentId(String invoiceDocumentId) {
        this.invoiceDocumentId = invoiceDocumentId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Boolean getAllowInvoiceGeneration() {
        return allowInvoiceGeneration;
    }

    public void setAllowInvoiceGeneration(Boolean allowInvoiceGeneration) {
        this.allowInvoiceGeneration = allowInvoiceGeneration;
    }

    public Boolean getModuleB2BEnabled() {
        return moduleB2BEnabled;
    }

    public void setModuleB2BEnabled(Boolean moduleB2BEnabled) {
        this.moduleB2BEnabled = moduleB2BEnabled;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public IdCodeDTO getCurrency() {
        return currency;
    }

    public void setCurrency(IdCodeDTO currency) {
        this.currency = currency;
    }

    public Boolean getAllowSecMKT() {
        return allowSecMKT;
    }

    public void setAllowSecMKT(Boolean allowSecMKT) {
        this.allowSecMKT = allowSecMKT;
    }

    public Boolean getUseMultiCurrency() {
        return useMultiCurrency;
    }

    public void setUseMultiCurrency(Boolean useMultiCurrency) {
        this.useMultiCurrency = useMultiCurrency;
    }
}
