package es.onebox.event.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EntityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5381446959307112066L;

    private Integer id;
    private String name;
    private String shortName;
    private String description;
    private String nif;
    private IdDTO language;
    private List<IdDTO> selectedLanguages;
    private String socialReason;
    private String address;
    private String city;
    private Integer countryId;
    private Integer countrySubdivisionId;
    private String postalCode;
    private EntityState state;
    private Boolean useExternalManagement;
    private EntityDTO operator;
    private String logoUrl;
    private String corporateColor;
    private Boolean allowLoyaltyPoints;
    private Boolean allowHardTicketPDF;
    private OperatorCurrenciesDTO currencies;
    private Boolean useSecondaryMarket;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public IdDTO getLanguage() {
        return language;
    }

    public void setLanguage(IdDTO language) {
        this.language = language;
    }

    public List<IdDTO> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(List<IdDTO> selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public EntityState getState() {
        return state;
    }

    public Boolean getUseExternalManagement() {
        return useExternalManagement;
    }

    public void setUseExternalManagement(Boolean useExternalManagement) {
        this.useExternalManagement = useExternalManagement;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public EntityDTO getOperator() {
        return operator;
    }

    public void setOperator(EntityDTO operator) {
        this.operator = operator;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCorporateColor() {
        return corporateColor;
    }

    public void setCorporateColor(String corporateColor) {
        this.corporateColor = corporateColor;
    }

    public Boolean getAllowLoyaltyPoints() {
        return allowLoyaltyPoints;
    }

    public void setAllowLoyaltyPoints(Boolean allowLoyaltyPoints) {
        this.allowLoyaltyPoints = allowLoyaltyPoints;
    }

    public Boolean getAllowHardTicketPDF() {
        return allowHardTicketPDF;
    }

    public void setAllowHardTicketPDF(Boolean allowHardTicketPDF) {
        this.allowHardTicketPDF = allowHardTicketPDF;
    }

    public OperatorCurrenciesDTO getCurrencies() {
        return currencies;
    }

    public void setCurrencies(OperatorCurrenciesDTO currencies) {
        this.currencies = currencies;
    }

    public Boolean getUseSecondaryMarket() { return useSecondaryMarket; }

    public void setUseSecondaryMarket(Boolean useSecondaryMarket) { this.useSecondaryMarket = useSecondaryMarket; }


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
