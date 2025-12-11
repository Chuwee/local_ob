package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class VoucherGroupSearchFilter extends BaseEntityRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<VoucherStatus> status;

    private List<VoucherGroupType> type;

    @JsonProperty("q")
    private String freeSearch;

    private SortOperator<String> sort;

    @JsonProperty("currency_code")
    private String currencyCode;

    public List<VoucherStatus> getStatus() {
        return status;
    }

    public void setStatus(List<VoucherStatus> status) {
        this.status = status;
    }

    public List<VoucherGroupType> getType() {
        return type;
    }

    public void setType(List<VoucherGroupType> type) {
        this.type = type;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
