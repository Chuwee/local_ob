package es.onebox.mgmt.export.dto;

import es.onebox.core.file.exporter.generator.export.Translation;
import es.onebox.mgmt.export.enums.FileFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ExportRequest<T extends ExportFileField> implements Serializable {

    @Serial
    private static final long serialVersionUID = -308933635314402213L;

    @NotEmpty(message = "fields can not be empty")
    @Valid
    private List<T> fields;
    @NotNull(message = "format can not be null")
    private FileFormat format;
    @NotNull(message = "delivery can not be null")
    @Valid
    private Delivery delivery;
    @Valid
    private Set<Translation> translations;


    public List<T> getFields() {
        return fields;
    }
    public void setFields(List<T> fields) {
        this.fields = fields;
    }

    public FileFormat getFormat() {
        return format;
    }
    public void setFormat(FileFormat format) {
        this.format = format;
    }

    public Delivery getDelivery() {
        return delivery;
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Set<Translation> getTranslations() {
        return translations;
    }
    public void setTranslations(Set<Translation> translations) {
        this.translations = translations;
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
