package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.common.datasources.ms.entity.enums.EntityState;
import es.onebox.common.datasources.ms.entity.enums.EntityType;
import es.onebox.common.entities.dto.IdValueDTO;
import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.core.serializer.dto.common.IdDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EntityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1356447139140800768L;
    private Long id;
    private String name;
    private EntityState state;
    private IdCodeDTO language;
    private EntityDTO operator;
    private List<EntityType> types;
    private List<IdDTO> selectedCategories;
    private IdValueDTO timezone;
    private String description;
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
    private Integer invoiceCountryId;

    private String invoicePostalCode;
    private String defaultLanguage;
    private Boolean allowInvoiceGeneration;
    private Boolean moduleB2BEnabled;
    private CurrencyDTO currency;
    private String shortName;
    private Boolean allowSecMKT;
    private String nif;
    private Integer countryId;
    private OperatorCurrencies currencies;
    private String shard;
    private String externalReference;
    private Map<String, Object> externalData;
    private Boolean allowFeverZone;
    private String phone;
    private String email;


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

    public EntityState getState() {
        return state;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public IdCodeDTO getLanguage() {
        return language;
    }

    public void setLanguage(IdCodeDTO language) {
        this.language = language;
    }

    public EntityDTO getOperator() {
        return operator;
    }

    public void setOperator(EntityDTO operator) {
        this.operator = operator;
    }

    public List<EntityType> getTypes() {
        return types;
    }

    public void setTypes(List<EntityType> types) {
        this.types = types;
    }

    public List<IdDTO> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<IdDTO> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public IdValueDTO getTimezone() {
		return timezone;
	}

	public void setTimezone(IdValueDTO timezone) {
		this.timezone = timezone;
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getUsingExternalManagement() {
        return usingExternalManagement;
    }

    public void setUsingExternalManagement(Boolean usingExternalManagement) {
        this.usingExternalManagement = usingExternalManagement;
    }

    public TimeZoneGroupDTO getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public void setTimeZoneGroup(TimeZoneGroupDTO timeZoneGroup) {
        this.timeZoneGroup = timeZoneGroup;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public String getInvoiceDocumentId() {
        return invoiceDocumentId;
    }

    public void setInvoiceDocumentId(String invoiceDocumentId) {
        this.invoiceDocumentId = invoiceDocumentId;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getInvoiceCity() {
        return invoiceCity;
    }

    public void setInvoiceCity(String invoiceCity) {
        this.invoiceCity = invoiceCity;
    }

    public Integer getInvoiceCountryId() {
        return invoiceCountryId;
    }

    public void setInvoiceCountryId(Integer invoiceCountryId) {
        this.invoiceCountryId = invoiceCountryId;
    }

    public String getInvoicePostalCode() {
        return invoicePostalCode;
    }

    public void setInvoicePostalCode(String invoicePostalCode) {
        this.invoicePostalCode = invoicePostalCode;
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

    public CurrencyDTO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDTO currency) {
        this.currency = currency;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getAllowSecMKT() {
        return allowSecMKT;
    }

    public void setAllowSecMKT(Boolean allowSecMKT) {
        this.allowSecMKT = allowSecMKT;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public OperatorCurrencies getCurrencies() {
        return currencies;
    }

    public void setCurrencies(OperatorCurrencies currencies) {
        this.currencies = currencies;
    }

    public String getShard() {
        return shard;
    }

    public void setShard(String shard) {
        this.shard = shard;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public Boolean getAllowFeverZone() {
        return allowFeverZone;
    }

    public void setAllowFeverZone(Boolean allowFeverZone) {
        this.allowFeverZone = allowFeverZone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
