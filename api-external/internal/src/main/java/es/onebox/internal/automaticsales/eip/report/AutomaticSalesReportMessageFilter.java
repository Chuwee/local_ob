package es.onebox.internal.automaticsales.eip.report;

import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;
import es.onebox.core.file.exporter.generator.amqp.ExportMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class AutomaticSalesReportMessageFilter extends AutomaticSalesReportFilter implements ExportMessage<ApiExternalExportType> {

    @Serial
    private static final long serialVersionUID = 9015398219898416323L;
    private ApiExternalExportType exportType;

    @Override
    public String getMessageName() {
        return null;
    }

    @Override
    public String getRoutingKey() {
        return null;
    }

    @Override
    public ApiExternalExportType getExportType() {
        return this.exportType;
    }

    public void setExportType(ApiExternalExportType exportType) {
        this.exportType = exportType;
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
