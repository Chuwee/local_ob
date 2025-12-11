package es.onebox.mgmt.export.dto;

import es.onebox.core.file.exporter.generator.export.Translation;
import es.onebox.mgmt.export.enums.FileFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Set;

public class BaseExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private FileFormat format;
    private Delivery delivery;
    private ExportSettings settings;
    @Valid
    private Set<Translation> translations;

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

    public ExportSettings getSettings() {
        return settings;
    }
    public void setSettings(ExportSettings settings) {
        this.settings = settings;
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
