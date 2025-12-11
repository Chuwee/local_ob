package es.onebox.event.catalog.dao.couch;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.List;

public class CatalogRate extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -3245860872916345653L;
    private boolean defaultRate;
    private List<CatalogPriceType> priceTypes;

    public CatalogRate() {
    }

    public CatalogRate(Long id, String name, List<CatalogPriceType> priceTypes) {
        super(id, name);
        this.priceTypes = priceTypes;
    }

    public List<CatalogPriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<CatalogPriceType> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
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
