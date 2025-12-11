package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.VoucherExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.vouchers.enums.VoucherField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class VoucherExportFileField extends ExportFileField<VoucherField> {

    @JsonDeserialize(using = VoucherExportFieldDeserializer.class)
    @NotNull
    private VoucherField field;

    @Override
    public VoucherField getField() {
        return field;
    }

    @Override
    public void setField(VoucherField field) {
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
}
