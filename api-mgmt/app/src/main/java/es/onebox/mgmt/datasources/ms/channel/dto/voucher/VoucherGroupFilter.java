package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(20)
public class VoucherGroupFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long entityId;
    private Long entityAdminId;
    private List<VoucherGroupType> types;
    private List<VoucherStatus> status;
    private String q;
    private SortOperator<String> sort;
    private Long currencyId;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public List<VoucherStatus> getStatus() {
        return status;
    }

    public void setStatus(List<VoucherStatus> status) {
        this.status = status;
    }

    public List<VoucherGroupType> getTypes() {
        return types;
    }

    public void setTypes(List<VoucherGroupType> types) {
        this.types = types;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
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
