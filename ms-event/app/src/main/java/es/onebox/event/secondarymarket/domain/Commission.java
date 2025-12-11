package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;

public class Commission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double percentage;

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
