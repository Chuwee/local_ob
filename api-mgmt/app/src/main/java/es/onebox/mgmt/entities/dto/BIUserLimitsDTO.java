package es.onebox.mgmt.entities.dto;

import es.onebox.mgmt.common.LimitDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class BIUserLimitsDTO implements Serializable {

    private static final long serialVersionUID = 2344444597399046651L;

    private LimitDTO basic;
    private LimitDTO advanced;
    private LimitDTO mobile;

    public LimitDTO getBasic() {
        return basic;
    }

    public void setBasic(LimitDTO basic) {
        this.basic = basic;
    }

    public LimitDTO getAdvanced() {
        return advanced;
    }

    public void setAdvanced(LimitDTO advanced) {
        this.advanced = advanced;
    }

    public LimitDTO getMobile() {
        return mobile;
    }

    public void setMobile(LimitDTO mobile) {
        this.mobile = mobile;
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
