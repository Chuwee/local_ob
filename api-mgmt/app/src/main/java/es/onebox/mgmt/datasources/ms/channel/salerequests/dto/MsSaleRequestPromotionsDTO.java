package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionStatus;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class MsSaleRequestPromotionsDTO implements Serializable{
    private static final long serialVersionUID = 8779567641588437208L;

    private Long id;
    private String name;
    private PromotionType type;
    private PromotionStatus status;
    private MsPriceVariationDTO priceVariation;
    private MsValidityPeriodDTO validityPeriod;
    private MsCollectiveDTO collective;
    private List<MsSessionSaleRequestDTO> sessions;
    private List<MsPriceTypeDTO> priceTypes;
    private List<MsRateDTO> rates;
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

    public MsPriceVariationDTO getPriceVariation() {
        return priceVariation;
    }

    public void setPriceVariation(MsPriceVariationDTO priceVariation) {
        this.priceVariation = priceVariation;
    }

    public MsValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(MsValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public MsCollectiveDTO getCollective() {
        return collective;
    }

    public void setCollective(MsCollectiveDTO collective) {
        this.collective = collective;
    }

    public List<MsSessionSaleRequestDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<MsSessionSaleRequestDTO> sessions) {
        this.sessions = sessions;
    }

    public List<MsPriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<MsPriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public List<MsRateDTO> getRates() {
        return rates;
    }

    public void setRates(List<MsRateDTO> rates) {
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
