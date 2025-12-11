package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

public class MaxBuyingLimitDTO implements Serializable {
    private static final long serialVersionUID = 3281070395518800434L;

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
