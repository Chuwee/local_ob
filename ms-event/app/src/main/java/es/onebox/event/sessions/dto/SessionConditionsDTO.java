package es.onebox.event.sessions.dto;

import java.io.Serializable;
import java.util.Map;

public class SessionConditionsDTO implements Serializable {

    private static final long serialVersionUID = 9120914938673587137L;

    private Long id;
    private Map<String, PriceTypeAndRateConditionDTO> refundPercentages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, PriceTypeAndRateConditionDTO> getRefundPercentages() {
        return refundPercentages;
    }

    public void setRefundPercentages(Map<String, PriceTypeAndRateConditionDTO> refundPercentages) {
        this.refundPercentages = refundPercentages;
    }
}
