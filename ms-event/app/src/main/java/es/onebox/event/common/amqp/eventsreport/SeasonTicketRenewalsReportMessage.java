package es.onebox.event.common.amqp.eventsreport;

import es.onebox.core.file.exporter.generator.amqp.ExportMessage;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SeasonTicketRenewalsReportMessage extends SeasonTicketRenewalsReportSearchRequest implements ExportMessage<MsEventReportType> {
    private static final long serialVersionUID = 1L;

    private MsEventReportType exportType;

    @Override
    public MsEventReportType getExportType() {
        return this.exportType;
    }

    public void setExportType(MsEventReportType exportType) {
        this.exportType = exportType;
    }

    @Override
    public String getMessageName() {
        return null;
    }

    @Override
    public String getRoutingKey() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
