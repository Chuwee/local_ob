package es.onebox.mgmt.vouchers.dto;

import es.onebox.mgmt.common.AggregationMetric;

import java.io.Serializable;
import java.util.List;

public class VoucherAggregationDataDTO implements Serializable {

    private List<AggregationMetric> overall;
    private List<VoucherAggregationDataTypeDTO> type;

    public List<AggregationMetric> getOverall() {
        return overall;
    }

    public void setOverall(List<AggregationMetric> overall) {
        this.overall = overall;
    }

    public List<VoucherAggregationDataTypeDTO> getType() {
        return type;
    }

    public void setType(List<VoucherAggregationDataTypeDTO> type) {
        this.type = type;
    }
}
