package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class SeasonSessionTransferDTO implements Serializable {
    private static final long serialVersionUID = -6490042174336830290L;

    private SeasonProductTransferStatus status;
    private Map<String, String> data;
    private Long count;
    private Long totalResends;

    public SeasonProductTransferStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonProductTransferStatus status) {
        this.status = status;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getTotalResends() {
        return totalResends;
    }

    public void setTotalResends(Long totalResends) {
        this.totalResends = totalResends;
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
