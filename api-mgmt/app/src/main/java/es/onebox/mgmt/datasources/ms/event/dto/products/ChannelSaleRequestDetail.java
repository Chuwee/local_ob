package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelSaleRequestDetail extends ChannelSaleRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3232235512010021507L;

    private CategoriesSaleRequest category;

    public CategoriesSaleRequest getCategory() {
        return category;
    }

    public void setCategory(CategoriesSaleRequest category) {
        this.category = category;
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
