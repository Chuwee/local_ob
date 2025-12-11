package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerLimits implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer limit;
    private List<String> excludedCustomerTypes;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<String> getExcludedCustomerTypes() {
        return excludedCustomerTypes;
    }

    public void setExcludedCustomerTypes(List<String> excludedCustomerTypes) {
        this.excludedCustomerTypes = excludedCustomerTypes;
    }
}
