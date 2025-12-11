package es.onebox.mgmt.datasources.ms.event.dto.secondarymarket;

import java.io.Serializable;

public class Commission implements Serializable {
    private Double percentage;

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double getPercentage) {
        this.percentage = getPercentage;
    }
}
