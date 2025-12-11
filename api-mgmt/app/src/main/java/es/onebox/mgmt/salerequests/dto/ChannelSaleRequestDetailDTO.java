package es.onebox.mgmt.salerequests.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelSaleRequestDetailDTO extends ChannelSaleRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3232235512010021507L;

    private CategoriesSaleRequestDTO category;

    public CategoriesSaleRequestDTO getCategory() {
        return category;
    }

    public void setCategory(CategoriesSaleRequestDTO category) {
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
