package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.vouchers.dto.VoucherStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class VoucherFilter extends BaseRequestFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long entityId;
    private Long voucherGroupId;
    private List<VoucherStatus> status;
    private String pin;
    private String email;
    private String freeSearch;
    private Boolean aggs;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getVoucherGroupId() {
        return voucherGroupId;
    }

    public void setVoucherGroupId(Long voucherGroupId) {
        this.voucherGroupId = voucherGroupId;
    }

    public List<VoucherStatus> getStatus() {
        return status;
    }

    public void setStatus(List<VoucherStatus> status) {
        this.status = status;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public Boolean getAggs() {
        return aggs;
    }

    public void setAggs(Boolean aggs) {
        this.aggs = aggs;
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
