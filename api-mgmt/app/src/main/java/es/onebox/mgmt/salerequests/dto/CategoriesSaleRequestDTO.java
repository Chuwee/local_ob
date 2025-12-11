package es.onebox.mgmt.salerequests.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CategoriesSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = -3577391140469425702L;

    private BaseCategorySaleRequestDTO parent;
    private BaseCategorySaleRequestDTO custom;

    public BaseCategorySaleRequestDTO getParent() {
        return parent;
    }

    public void setParent(BaseCategorySaleRequestDTO parent) {
        this.parent = parent;
    }

    public BaseCategorySaleRequestDTO getCustom() {
        return custom;
    }

    public void setCustom(BaseCategorySaleRequestDTO custom) {
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
