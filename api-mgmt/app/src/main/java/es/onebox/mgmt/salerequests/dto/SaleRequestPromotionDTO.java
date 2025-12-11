package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.common.promotions.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SaleRequestPromotionDTO implements Serializable{

    private static final long serialVersionUID = 3403612894080201323L;

    private Long id;
    private String name;
    private PromotionType type;
    private PromotionStatus status;
    @JsonProperty("price_variation")
    private PriceVariationDTO priceVariation;
    @JsonProperty("validity_period")
    private ValidityPeriodDTO validityPeriod;
    private CollectiveDTO collective;
    private List<BaseSessionSaleRequestDTO> sessions;
    @JsonProperty("price_types")
    private List<PriceTypeDTO> priceTypes;
    private List<RateDTO> rates;
    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;

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

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public PriceVariationDTO getPriceVariation() {
        return priceVariation;
    }

    public void setPriceVariation(PriceVariationDTO priceVariation) {
        this.priceVariation = priceVariation;
    }

    public ValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public CollectiveDTO getCollective() {
        return collective;
    }

    public void setCollective(CollectiveDTO collective) {
        this.collective = collective;
    }

    public List<BaseSessionSaleRequestDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<BaseSessionSaleRequestDTO> sessions) {
        this.sessions = sessions;
    }

    public List<PriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<PriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
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
