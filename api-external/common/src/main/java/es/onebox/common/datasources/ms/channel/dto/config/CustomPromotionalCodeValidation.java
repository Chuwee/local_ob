package es.onebox.common.datasources.ms.channel.dto.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomPromotionalCodeValidation implements Serializable {

    @Serial
    private static final long serialVersionUID = 5532493929461507566L;

    private String serviceImpl;

    private List<Integer> salesId;

    public String getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public List<Integer> getSalesId() { return salesId; }

    public void setSalesId(List<Integer> salesId) { this.salesId = salesId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
