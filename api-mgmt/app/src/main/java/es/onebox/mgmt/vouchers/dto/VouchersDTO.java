package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VouchersDTO extends BaseResponseCollection<VoucherDTO, Metadata> implements DateConvertible {

    private static final long serialVersionUID = 1L;

    @JsonProperty("aggregated_data")
    private VoucherAggregationDataDTO aggregations;

    public VoucherAggregationDataDTO getAggregations() {
        return aggregations;
    }

    public void setAggregations(VoucherAggregationDataDTO aggregations) {
        this.aggregations = aggregations;
    }

    @Override
    public void convertDates() {
        if (!CommonUtils.isEmpty(getData())) {
            for (VoucherDTO voucher : getData()) {
                voucher.convertDates();
            }
        }
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
