package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.export.deserializer.VenueTemplateSeatExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.venues.enums.VenueTemplateSeatField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class VenueTemplatesSeatExportFileField extends ExportFileField<VenueTemplateSeatField> {

    @JsonDeserialize(using = VenueTemplateSeatExportFieldDeserializer.class)
    @NotNull
    private VenueTemplateSeatField fields;

    @Override
    public VenueTemplateSeatField getField() {
        return fields;
    }

    @Override
    public void setField(VenueTemplateSeatField fields) {
        this.fields = fields;
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
