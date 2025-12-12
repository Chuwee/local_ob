package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;

public class SessionConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sessionId;
    private PreSaleConfigDTO preSaleConfig;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public PreSaleConfigDTO getPreSaleConfig() {
        return preSaleConfig;
    }

    public void setPreSaleConfig(PreSaleConfigDTO preSaleConfig) {
        this.preSaleConfig = preSaleConfig;
    }
}
