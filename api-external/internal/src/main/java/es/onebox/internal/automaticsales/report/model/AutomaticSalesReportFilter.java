package es.onebox.internal.automaticsales.report.model;

import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class AutomaticSalesReportFilter extends ExportWithEmailAndTimeZoneFilter<AutomaticSalesFileField, ApiExternalExportType> {

    @Serial
    private static final long serialVersionUID = 7012209173164523824L;
    private Long sessionId;
    private String q;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
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
