package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OperatorCurrencies implements Serializable {

    @Serial
    private static final long serialVersionUID = -3443966480792600167L;
    private List<Currency> selected;
    private String defaultCurrency;

    public List<Currency> getSelected() {
        return selected;
    }

    public void setSelected(List<Currency> selected) {
        this.selected = selected;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
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
