package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class MaxBuyingLimit implements Serializable {
    private static final long serialVersionUID = -6977661019530294583L;

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
