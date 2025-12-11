package es.onebox.mgmt.insurance.dto;

import java.io.Serial;
import java.io.Serializable;

public class InsuranceRangeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5452278205261960235L;
    private Double from;
    private Double to;
    private RangeValueDTO values = new RangeValueDTO();

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

    public RangeValueDTO getValues() {
        return values;
    }

    public void setValues(RangeValueDTO values) {
        this.values = values;
    }
}
