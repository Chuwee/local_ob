package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsValidityPeriodType;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class MsValidityPeriodDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private MsValidityPeriodType type;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public MsValidityPeriodType getType() {
        return type;
    }

    public void setType(MsValidityPeriodType type) {
        this.type = type;
    }

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }
}
