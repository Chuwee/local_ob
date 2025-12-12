package es.onebox.ms.notification.ie.orderrelease.dto;

import java.io.Serializable;

public class TimeZoneGroupDTO implements Serializable {

    private Integer zoneId;
    private String olsonId;

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

}
