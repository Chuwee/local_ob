package es.onebox.internal.automaticsales.filemanagement.dto;

import es.onebox.internal.automaticsales.export.dto.ExportRequest;
import es.onebox.internal.automaticsales.processsales.dto.AutomaticSalesFileField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class AutomaticSalesExportRequest extends ExportRequest<AutomaticSalesFileField> {

    @Serial
    private static final long serialVersionUID = -1592277524790168150L;

    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
