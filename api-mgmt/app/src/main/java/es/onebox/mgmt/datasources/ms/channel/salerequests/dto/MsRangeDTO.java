package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import java.io.Serializable;

public class MsRangeDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private Double from;
    private Double to;
    private Double value;

    public MsRangeDTO() {}

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
