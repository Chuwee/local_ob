package es.onebox.common.datasources.ms.event.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketRenewalInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1949382005934651311L;

    private Boolean autoRenewal;

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
}