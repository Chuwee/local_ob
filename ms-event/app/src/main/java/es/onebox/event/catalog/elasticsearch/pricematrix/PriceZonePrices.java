package es.onebox.event.catalog.elasticsearch.pricematrix;

import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceZonePrices implements Serializable {

    @Serial
    private static final long serialVersionUID = 6097716958357182326L;

    private Long priceZoneId;
    private List<RatePrices> rates;
    private List<DynamicPriceTranslationDTO> dynamicPriceTranslations;

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public List<RatePrices> getRates() {
        return rates;
    }

    public List<DynamicPriceTranslationDTO> getDynamicPriceTranslations() {
        return dynamicPriceTranslations;
    }

    public void setDynamicPriceTranslations(List<DynamicPriceTranslationDTO> dynamicPriceTranslations) {
        this.dynamicPriceTranslations = dynamicPriceTranslations;
    }

    public void setRates(List<RatePrices> rates) {
        this.rates = rates;
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
