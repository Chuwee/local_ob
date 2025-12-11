package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Vouchers extends BaseResponseCollection<Voucher, Metadata> implements Serializable {

    private static final long serialVersionUID = 1L;

    private VouchersAgg summaryData;

    public VouchersAgg getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(VouchersAgg summaryData) {
        this.summaryData = summaryData;
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
