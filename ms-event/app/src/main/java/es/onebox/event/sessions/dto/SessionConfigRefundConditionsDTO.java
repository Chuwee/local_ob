package es.onebox.event.sessions.dto;

import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;

import java.io.Serializable;
import java.util.Map;

public class SessionConfigRefundConditionsDTO implements Serializable {

    private static final long serialVersionUID = 5507613308473790274L;

    private Boolean printRefundPrice;
    private Map<Long, SessionConditionsMap> seasonPassRefundConditions;

    public Boolean getPrintRefundPrice() {
        return printRefundPrice;
    }

    public void setPrintRefundPrice(Boolean printRefundPrice) {
        this.printRefundPrice = printRefundPrice;
    }

    public Map<Long, SessionConditionsMap> getSeasonPassRefundConditions() {
        return seasonPassRefundConditions;
    }

    public void setSeasonPassRefundConditions(Map<Long, SessionConditionsMap> seasonPassRefundConditions) {
        this.seasonPassRefundConditions = seasonPassRefundConditions;
    }
}
