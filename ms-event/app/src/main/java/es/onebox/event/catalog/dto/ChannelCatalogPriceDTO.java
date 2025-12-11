package es.onebox.event.catalog.dto;

import es.onebox.event.catalog.elasticsearch.pricematrix.Price;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelCatalogPriceDTO extends Price {

    private static final long serialVersionUID = -3898774220201542002L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
