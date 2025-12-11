package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.b2b.publishing.enums.SeatPublishingFileField;
import es.onebox.mgmt.export.deserializer.SeatPublishingExportFieldDeserializer;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SeatPublishingExportFileField implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = SeatPublishingExportFieldDeserializer.class)
    @NotNull
    private SeatPublishingFileField field;

    private String name;

    public SeatPublishingFileField getField() {
        return field;
    }

    public void setField(SeatPublishingFileField field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
