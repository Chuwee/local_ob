package es.onebox.mgmt.channels.purchaseconfig.dto;

import es.onebox.mgmt.datasources.ms.channel.enums.PriceDisplayMode;
import es.onebox.mgmt.datasources.ms.channel.enums.TaxesDisplayMode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPriceDisplayConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PriceDisplayMode prices;
    private TaxesDisplayMode taxes;

    public ChannelPriceDisplayConfigDTO() {
    }

    public ChannelPriceDisplayConfigDTO(PriceDisplayMode prices, TaxesDisplayMode taxes) {
        this.prices = prices;
        this.taxes = taxes;
    }

    public PriceDisplayMode getPrices() {
        return prices;
    }

    public void setPrices(PriceDisplayMode prices) {
        this.prices = prices;
    }

    public TaxesDisplayMode getTaxes() {
        return taxes;
    }

    public void setTaxes(TaxesDisplayMode taxes) {
        this.taxes = taxes;
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
