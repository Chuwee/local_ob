package es.onebox.mgmt.vouchers.dto;

import es.onebox.mgmt.common.LimitlessValueDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class VoucherUsageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer used;
    private LimitlessValueDTO limit;

    public VoucherUsageDTO() {
    }

    public VoucherUsageDTO(Integer used, LimitlessValueDTO limit) {
        this.used = used;
        this.limit = limit;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public LimitlessValueDTO getLimit() {
        return limit;
    }

    public void setLimit(LimitlessValueDTO limit) {
        this.limit = limit;
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
