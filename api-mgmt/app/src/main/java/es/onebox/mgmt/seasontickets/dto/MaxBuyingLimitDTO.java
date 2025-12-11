package es.onebox.mgmt.seasontickets.dto;

import java.io.Serializable;

public class MaxBuyingLimitDTO implements Serializable {

    private static final long serialVersionUID = 3268382188285071245L;

    private Boolean override;

    private Integer value;

    public Boolean getOverride() {
        return override;
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
