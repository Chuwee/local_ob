package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;
import java.util.Map;

public class SessionConditions implements Serializable {

    private static final long serialVersionUID = 9120914938673587137L;

    private Long id;
    private Map<String, PriceTypeAndRateCondition> refundPercentages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, PriceTypeAndRateCondition> getRefundPercentages() {
        return refundPercentages;
    }

    public void setRefundPercentages(Map<String, PriceTypeAndRateCondition> refundPercentages) {
        this.refundPercentages = refundPercentages;
    }
}
