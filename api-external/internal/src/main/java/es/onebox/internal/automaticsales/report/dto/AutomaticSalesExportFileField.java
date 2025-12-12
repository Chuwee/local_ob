package es.onebox.internal.automaticsales.report.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.internal.automaticsales.export.dto.ExportFileField;
import es.onebox.internal.automaticsales.report.deserializer.AutomaticSalesExportFieldDeserializer;
import es.onebox.internal.automaticsales.report.enums.AutomaticSalesFields;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;

public class AutomaticSalesExportFileField extends ExportFileField<AutomaticSalesFields> {

    @Serial
    private static final long serialVersionUID = -4171665640604209743L;

    @JsonDeserialize(using = AutomaticSalesExportFieldDeserializer.class)
    @NotNull
    private AutomaticSalesFields field;

    @Override
    public AutomaticSalesFields getField() {
        return field;
    }

    @Override
    public void setField(AutomaticSalesFields field) {
        this.field = field;
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
