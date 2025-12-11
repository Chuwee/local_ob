package es.onebox.event.report.model.filter;

import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.report.PriceSimulationFileField;
import java.io.Serial;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PriceSimulationReportRequest extends
    ExportWithEmailAndTimeZoneFilter<PriceSimulationFileField, MsEventReportType> {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long saleRequestId;

    public Long getSaleRequestId() {
        return saleRequestId;
    }

    public void setSaleRequestId(Long saleRequestId) {
        this.saleRequestId = saleRequestId;
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
