package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class TimeZoneGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private Integer zoneId;
    private String olsonId;

    public Integer getZoneId() {
        return this.zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getOlsonId() {
        return this.olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
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
