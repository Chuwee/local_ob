package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(100)
@DefaultLimit(50)
public class VoucherSearchFilter extends BaseEntityRequestFilter {

    private static final long serialVersionUID = 1L;

    private List<VoucherStatus> status;

    private String pin;

    private String email;

    @JsonProperty("q")
    private String freeSearch;

    private Boolean aggs;

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
