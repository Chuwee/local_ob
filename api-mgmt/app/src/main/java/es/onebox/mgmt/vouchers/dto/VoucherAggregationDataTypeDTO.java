package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.AggregationMetric;

import java.io.Serializable;
import java.util.List;

public class VoucherAggregationDataTypeDTO implements Serializable {

    @JsonProperty("agg_value")
    private VoucherAggregationTypeDTO aggValue;
    @JsonProperty("agg_metric")
    private List<AggregationMetric> aggMetric;

    public VoucherAggregationTypeDTO getAggValue() {
        return aggValue;
    }

    public void setAggValue(VoucherAggregationTypeDTO aggValue) {
        this.aggValue = aggValue;
    }

    public List<AggregationMetric> getAggMetric() {
        return aggMetric;
    }

    public void setAggMetric(List<AggregationMetric> aggMetric) {
        this.aggMetric = aggMetric;
    }
}
