package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CategoriesSaleRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -3577391140469425702L;

    private BaseCategorySaleRequest parent;
    private BaseCategorySaleRequest custom;

    public BaseCategorySaleRequest getParent() {
        return parent;
    }

    public void setParent(BaseCategorySaleRequest parent) {
        this.parent = parent;
    }

    public BaseCategorySaleRequest getCustom() {
        return custom;
    }

    public void setCustom(BaseCategorySaleRequest custom) {
        this.custom = custom;
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
