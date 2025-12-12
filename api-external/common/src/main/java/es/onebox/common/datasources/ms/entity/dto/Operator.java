package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class Operator extends EntityDTO {

    @Serial
    private static final long serialVersionUID = 3945633059283539063L;
    private List<String> gateways = new ArrayList<>();

    private Boolean useMultiCurrency;

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public Boolean getUseMultiCurrency() {
        return useMultiCurrency;
    }

    public void setUseMultiCurrency(Boolean useMultiCurrency) {
        this.useMultiCurrency = useMultiCurrency;
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
